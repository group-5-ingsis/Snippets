package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.JsonUtil
import com.ingsis.snippets.async.producer.format.FormatRequest
import com.ingsis.snippets.async.producer.format.FormattedSnippetConsumer
import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.async.producer.lint.LintRequest
import com.ingsis.snippets.async.producer.lint.LintRequestProducer
import com.ingsis.snippets.async.producer.lint.LintResultConsumer
import com.ingsis.snippets.rules.Rule
import com.ingsis.snippets.rules.RuleManager
import com.ingsis.snippets.user.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetService: AssetService,
  private val permissionService: PermissionService,
  private val snippetFormatProducer: SnippetFormatProducer,
  private val formattedSnippetConsumer: FormattedSnippetConsumer,
  private val lintRequestProducer: LintRequestProducer,
  private val lintResultConsumer: LintResultConsumer
) {

  suspend fun createSnippet(userId: String, username: String, snippetDto: SnippetDto): Snippet {
    val compliance = lintSnippet(username, snippetDto.content)
    val snippet = Snippet(snippetDto, compliance)

    snippet.author = username

    val savedSnippet = snippetRepository.save(snippet)
    val createdSnippetId = savedSnippet.id

    val asset = Asset(
      container = username,
      key = createdSnippetId,
      content = snippetDto.content
    )

    permissionService.updatePermissions(userId, createdSnippetId, "read")
    permissionService.updatePermissions(username, createdSnippetId, "write")

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  suspend fun lintSnippet(username: String, content: String): String {
    val requestId = UUID.randomUUID().toString()
    val lintRequest = LintRequest(requestId, username, snippet = content)

    lintRequestProducer.publishEvent(lintRequest)

    val responseDeferred = lintResultConsumer.getLintResponseResponse(requestId)
    return responseDeferred.await()
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

  fun getSnippetsByName(userId: String, name: String): List<Snippet> {
    val mySnippetsIds = permissionService.getMySnippetsIds(userId)

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

  fun updateLintingRules(userData: UserData, rules: List<Rule>): List<Rule> {
    val rulesAsType = RuleManager.convertToLintingRules(rules)
    val rulesAsJson = JsonUtil.serializeLintingRules(rulesAsType)
    val newAsset = Asset(
      container = userData.username,
      key = "LintingRules",
      content = rulesAsJson
    )
    assetService.createOrUpdateAsset(newAsset)

    CoroutineScope(Dispatchers.IO).launch {
      lintAllSnippetsForUser(userData)
    }

    val newRules = assetService.getAssetContent(userData.username, "LintingRules")
    return RuleManager.convertToRuleList(JsonUtil.deserializeLintingRules(newRules))
  }

  fun formatAllSnippetsForUser(userData: UserData) {
    val snippets = getAllSnippetsForUser(userData.userId)

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

  fun lintAllSnippetsForUser(userData: UserData) {
    val snippets = getAllSnippetsForUser(userData.userId)

    snippets.forEach { snippet ->
      CoroutineScope(Dispatchers.IO).launch {
        val requestId = UUID.randomUUID().toString()
        val formatRequest = LintRequest(requestId, userData.username, snippet.content)

        lintRequestProducer.publishEvent(formatRequest)

        try {
          val responseDeferred = lintResultConsumer.getLintResponseResponse(requestId)
          val complianceResult = responseDeferred.await()

          updateSnippetCompliance(snippet.id, complianceResult)
        } catch (_: Exception) {
        }
      }
    }
  }

  fun getAllSnippetsForUser(userId: String): List<SnippetWithContent> {
    val snippetIds = permissionService.getSnippets(userId, "read")

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

  fun updateSnippetCompliance(id: String, status: String) {
    val existingSnippet = getSnippetById(id)
    existingSnippet.compliance = status
    snippetRepository.save(existingSnippet)
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
}
