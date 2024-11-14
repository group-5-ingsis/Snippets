package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.JsonUtil
import com.ingsis.snippets.async.producer.format.FormatRequest
import com.ingsis.snippets.async.producer.format.FormattedSnippetConsumer
import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.rules.Rule
import com.ingsis.snippets.rules.RuleManager
import com.ingsis.snippets.rules.RulesController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetService: AssetService,
  private val permissionService: PermissionService,
  private val snippetFormatProducer: SnippetFormatProducer,
  private val formattedSnippetConsumer: FormattedSnippetConsumer,
  private val rulesController: RulesController
) {

  suspend fun createSnippet(jwt: Jwt, snippetDto: SnippetDto): Snippet {
    val (userId, username) = extractUserInfo(jwt)

    val compliance = rulesController.lintSnippet(jwt, snippetDto.content)
    val snippet = Snippet(snippetDto, compliance)

    snippet.author = username

    val savedSnippet = snippetRepository.save(snippet)
    val createdSnippetId = savedSnippet.id

    val asset = Asset(
      container = username,
      key = createdSnippetId,
      content = snippetDto.content
    )

    val userData = UserData(userId, username)

    permissionService.updatePermissions(userData, createdSnippetId, "read")
    permissionService.updatePermissions(userData, createdSnippetId, "write")

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  fun getSnippetById(id: String): Snippet {
    return snippetRepository.findById(id).orElse(null)
  }

  fun getSnippetContent(id: String): SnippetWithContent {
    val snippet = getSnippetById(id)
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    val snippetWithContent = SnippetWithContent(snippet, content)
    return snippetWithContent
  }

  fun getSnippetsByName(name: String): List<Snippet> {
    val mySnippetsIds = permissionService.getMySnippetsIds()

    return if (name.isBlank()) {
      snippetRepository.findAll().filter { it.id in mySnippetsIds }
    } else {
      snippetRepository.findByName(name).filter { it.id in mySnippetsIds }
    }
  }

  fun getSnippets(): List<Snippet> {
    return snippetRepository.findAll()
  }

  fun getFormattingRules(username: String): List<Rule> {
    val rulesJson = assetService.getAssetContent(username, "FormattingRules")

    return if (rulesJson == "No Content") {
      val defaultFormattingRules = RuleManager.getDefaultFormattingRules()

      val asset = Asset(
        container = username,
        key = "FormattingRules",
        content = JsonUtil.serializeFormattingRules(defaultFormattingRules)
      )

      assetService.createOrUpdateAsset(asset)

      RuleManager.convertToRuleList(defaultFormattingRules)
    } else {
      val existingFormattingRules = JsonUtil.deserializeFormattingRules(rulesJson)

      RuleManager.convertToRuleList(existingFormattingRules)
    }
  }

  fun updateFormattingRules(userData: UserData, rules: List<Rule>): List<Rule> {
    val rulesAsType = RuleManager.convertToFormattingRules(rules)
    val rulesAsJson = JsonUtil.serializeFormattingRules(rulesAsType)
    val newAsset = Asset(
      container = userData.username,
      key = "FormattingRules",
      content = rulesAsJson
    )
    assetService.createOrUpdateAsset(newAsset)

    CoroutineScope(Dispatchers.IO).launch {
      formatAllSnippetsForUser(userData)
    }

    val newRules = assetService.getAssetContent(userData.username, "FormattingRules")
    return RuleManager.convertToRuleList(JsonUtil.deserializeFormattingRules(newRules))
  }

  fun formatAllSnippetsForUser(userData: UserData) {
    val snippets = getAllSnippetsForUser(userData)

    snippets.forEach { snippet ->
      CoroutineScope(Dispatchers.IO).launch {
        val requestId = UUID.randomUUID().toString()
        val formatRequest = FormatRequest(requestId, userData.username, snippet.content)

        snippetFormatProducer.publishEvent(formatRequest)

        try {
          val responseDeferred = formattedSnippetConsumer.getFormatResponse(requestId)
          val formattedContent = responseDeferred.await()

          updateSnippet(snippet.id, formattedContent)
        } catch (_: Exception) {
        }
      }
    }
  }

  fun getAllSnippetsForUser(userData: UserData): List<SnippetWithContent> {
    val snippetIds = permissionService.getMyWritableSnippets(userData)

    return snippetIds.mapNotNull { snippetId ->
      try {
        val snippet = getSnippetById(snippetId)
        val content = assetService.getAssetContent(snippet.author, snippet.id)
        SnippetWithContent(snippet, content)
      } catch (e: Exception) {
        println("Error retrieving snippet with ID $snippetId: ${e.message}")
        null
      }
    }
  }

  fun getLintingRules(username: String): List<Rule> {
    val rulesJson = assetService.getAssetContent(username, "LintingRules")

    return if (rulesJson == "No Content") {
      val defaultLintingRules = RuleManager.getDefaultLintingRules()

      val asset = Asset(
        container = username,
        key = "LintingRules",
        content = JsonUtil.serializeLintingRules(defaultLintingRules)
      )

      assetService.createOrUpdateAsset(asset)

      RuleManager.convertToRuleList(defaultLintingRules)
    } else {
      val existingFormattingRules = JsonUtil.deserializeLintingRules(rulesJson)

      RuleManager.convertToRuleList(existingFormattingRules)
    }
  }

  fun updateSnippet(id: String, newContent: String): SnippetWithContent {
    val existingSnippet = getSnippetById(id)

    val asset = Asset(
      container = existingSnippet.author,
      key = existingSnippet.id,
      content = newContent
    )

    assetService.createOrUpdateAsset(asset)

    return SnippetWithContent(existingSnippet, newContent)
  }

  fun deleteSnippet(id: String) {
    val snippet = getSnippetById(id)
    val container = snippet.author
    val key = snippet.id
    assetService.deleteAsset(container, key)
    snippetRepository.deleteById(id)
  }

  fun shareSnippet(userId: String, snippetId: String, userToShare: String): SnippetWithContent {
    val snippet = getSnippetById(snippetId)
    val writableSnippets = permissionService.getSnippets(userId, "write")
    if (snippet.id in writableSnippets) {
      permissionService.shareSnippet(userToShare, snippetId)
    }
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    return SnippetWithContent(snippet, content)
  }

  private fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims["https://snippets/claims/username"]?.toString() ?: "unknown"
    return Pair(userId, username)
  }
}
