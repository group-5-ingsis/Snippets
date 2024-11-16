package com.ingsis.snippets.rules

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.JsonUtil
import com.ingsis.snippets.async.format.FormatRequest
import com.ingsis.snippets.async.format.FormattedSnippetConsumer
import com.ingsis.snippets.async.format.SnippetFormatProducer
import com.ingsis.snippets.async.lint.LintRequest
import com.ingsis.snippets.async.lint.LintRequestProducer
import com.ingsis.snippets.async.lint.LintResultConsumer
import com.ingsis.snippets.snippet.PermissionService
import com.ingsis.snippets.snippet.SnippetService
import com.ingsis.snippets.snippet.SnippetWithContent
import com.ingsis.snippets.user.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import java.util.*

@Service
class RulesService(
  private val assetService: AssetService,
  private val snippetFormatProducer: SnippetFormatProducer,
  private val formattedSnippetConsumer: FormattedSnippetConsumer,
  private val lintRequestProducer: LintRequestProducer,
  private val lintResultConsumer: LintResultConsumer,
  private val permissionService: PermissionService,
  private val snippetService: SnippetService
) {

  suspend fun formatSnippet(username: String, content: String): String {
    val requestId = UUID.randomUUID().toString()
    val formatRequest = FormatRequest(requestId, username, snippet = content)

    snippetFormatProducer.publishEvent(formatRequest)

    val responseDeferred = formattedSnippetConsumer.getFormatResponse(requestId)
    return responseDeferred.await()
  }

  fun getFormattingRules(username: String): List<RuleDto> {
    val assetKey = "FormattingRules"
    val rulesJson = assetService.getAssetContent(username, assetKey)
    return if (rulesJson == "No Content") {
      val defaultRules = RuleManager.getDefaultFormattingRules()
      saveRules(username, assetKey, defaultRules)
      RuleManager.convertToRuleList(defaultRules)
    } else {
      val existingRules = JsonUtil.deserializeFormattingRules(rulesJson)
      RuleManager.convertToRuleList(existingRules)
    }
  }

  fun getLintingRules(username: String): List<RuleDto> {
    val rulesJson = assetService.getAssetContent(username, "LintingRules")
    return if (rulesJson == "No Content") {
      val defaultRules = RuleManager.getDefaultLintingRules()
      saveRules(username, "LintingRules", defaultRules)
      RuleManager.convertToRuleList(defaultRules)
    } else {
      val existingRules = JsonUtil.deserializeLintingRules(rulesJson)
      RuleManager.convertToRuleList(existingRules)
    }
  }

  fun updateFormattingRules(userData: UserData, ruleDtos: List<RuleDto>): List<RuleDto> {
    val rulesAsType = RuleManager.convertToFormattingRules(ruleDtos)
    saveRules(userData.username, "FormattingRules", rulesAsType)
    CoroutineScope(Dispatchers.IO).launch { formatAllSnippetsForUser(userData) }
    return getFormattingRules(userData.username)
  }

  fun updateLintingRules(userData: UserData, ruleDtos: List<RuleDto>): List<RuleDto> {
    val rulesAsType = RuleManager.convertToFormattingRules(ruleDtos)
    saveRules(userData.username, "LintingRules", rulesAsType)
    CoroutineScope(Dispatchers.IO).launch { lintAllSnippetsForUser(userData) }
    return getLintingRules(userData.username)
  }

  private fun saveRules(username: String, key: String, rules: Rules) {
    val rulesAsJson = when (rules) {
      is FormattingRules -> JsonUtil.serializeRules(rules)
      is LintingRules -> JsonUtil.serializeRules(rules)
      else -> throw IllegalArgumentException("Unsupported rules type")
    }
    val asset = Asset(container = username, key = key, content = rulesAsJson)
    assetService.createOrUpdateAsset(asset)
  }

  private fun lintAllSnippetsForUser(userData: UserData) {
    val snippets = getAllSnippets(userData.userId)
    snippets.forEach { snippet ->
      CoroutineScope(Dispatchers.IO).launch {
        val requestId = UUID.randomUUID().toString()
        val lintRequest = LintRequest(requestId, userData.username, snippet.content)
        lintRequestProducer.publishEvent(lintRequest)
        try {
          lintResultConsumer.getLintResponseResponse(requestId).await()
        } catch (e: Exception) {
          println("Error linting snippet ${snippet.id}: ${e.message}")
        }
      }
    }
  }

  private fun formatAllSnippetsForUser(userData: UserData) {
    val snippets = getWritableSnippets(userData.userId)
    snippets.forEach { snippet ->
      CoroutineScope(Dispatchers.IO).launch {
        val requestId = UUID.randomUUID().toString()
        val formatRequest = FormatRequest(requestId, userData.username, snippet.content)
        snippetFormatProducer.publishEvent(formatRequest)
        try {
          val formattedContent = formattedSnippetConsumer.getFormatResponse(requestId).await()
          snippetService.updateSnippet(snippet.id, formattedContent)
        } catch (e: Exception) {
          println("Error formatting snippet ${snippet.id}: ${e.message}")
        }
      }
    }
  }

  private fun getWritableSnippets(userId: String): List<SnippetWithContent> {
    val snippetIds = permissionService.getSnippets(userId, "write")
    return getSnippetContent(snippetIds)
  }

  private fun getSnippetContent(snippetIds: List<String>): List<SnippetWithContent> {
    return snippetIds.mapNotNull { snippetId ->
      try {
        val snippet = snippetService.getSnippetById(snippetId)
        val content = assetService.getAssetContent(snippet.author, snippet.id)
        SnippetWithContent(snippet, content)
      } catch (e: Exception) {
        println("Error retrieving snippet with ID $snippetId: ${e.message}")
        null
      }
    }
  }

  private fun getAllSnippets(userId: String): List<SnippetWithContent> {
    val writeSnippets = permissionService.getSnippets(userId, "write")
    val readSnippets = permissionService.getSnippets(userId, "read")
    val snippetIds = writeSnippets + readSnippets
    return getSnippetContent(snippetIds)
  }
}
