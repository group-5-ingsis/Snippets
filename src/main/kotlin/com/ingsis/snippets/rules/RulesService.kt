package com.ingsis.snippets.rules

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.FormatRequest
import com.ingsis.snippets.async.JsonUtil
import com.ingsis.snippets.async.LintRequest
import com.ingsis.snippets.async.format.FormatRequestProducer
import com.ingsis.snippets.async.format.FormatResponseConsumer
import com.ingsis.snippets.async.lint.LintRequestProducer
import com.ingsis.snippets.async.lint.LintResponseConsumer
import com.ingsis.snippets.snippet.*
import com.ingsis.snippets.user.PermissionService
import com.ingsis.snippets.user.UserData
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class RulesService(
  private val assetService: AssetService,
  private val formatRequestProducer: FormatRequestProducer,
  private val formatResponseConsumer: FormatResponseConsumer,
  private val lintRequestProducer: LintRequestProducer,
  private val lintResponseConsumer: LintResponseConsumer,
  private val permissionService: PermissionService,
  private val snippetService: SnippetService,
  private val snippetComplianceRepository: SnippetComplianceRepository
) {

  private val logger = LoggerFactory.getLogger(RulesService::class.java)

  suspend fun formatSnippet(username: String, content: String): String {
    val requestId = UUID.randomUUID().toString()
    val formatRequest = FormatRequest(requestId, username, snippet = content)

    logger.info("Received request to format snippet with requestId: $requestId")

    formatRequestProducer.publishEvent(formatRequest)

    logger.info("Published format request event with requestId: $requestId")

    val responseDeferred = formatResponseConsumer.getFormatResponse(requestId)
    return responseDeferred.await()
  }

  fun getRules(username: String, type: String): List<RuleDto> {
    val rulesJson = assetService.getAssetContent(username, type)
    return try {
      if (rulesJson == "No Content") {
        val defaultRules = RuleManager.getDefaultRules(type)
        saveRules(username, type, defaultRules)
        RuleManager.convertToRuleList(defaultRules)
      } else {
        val existingRules = JsonUtil.deserializeRules(rulesJson, type)
        RuleManager.convertToRuleList(existingRules)
      }
    } catch (e: Exception) {
      val defaultRules = RuleManager.getDefaultRules(type)
      saveRules(username, type, defaultRules)
      RuleManager.convertToRuleList(defaultRules)
    }
  }

  fun updateRules(userData: UserData, ruleDtos: List<RuleDto>, type: String): List<RuleDto> {
    val rulesAsType = RuleManager.convertToType(ruleDtos, type)
    saveRules(userData.username, type, rulesAsType)
    CoroutineScope(Dispatchers.IO).launch { updateSnippetsWithNewRules(userData, type) }
    return getRules(userData.username, type)
  }

  private fun updateSnippetsWithNewRules(userData: UserData, type: String) {
    when (type) {
      FORMATTING_KEY -> formatAllSnippetsForUser(userData)
      LINTING_KEY -> lintAllSnippetsForUser(userData)
      else -> logger.warn("Unknown rule type: $type")
    }
  }

  private fun saveRules(username: String, key: String, rules: Rules) {
    val rulesAsJson = JsonUtil.serializeRules(rules)
    val asset = Asset(container = username, key = key, content = rulesAsJson)
    assetService.createOrUpdateAsset(asset)
  }

  private fun lintAllSnippetsForUser(userData: UserData) {
    val snippets = getAllSnippets(userData.userId)

    runBlocking {
      withContext(Dispatchers.IO) {
        snippets.forEach { snippet ->
          val existingCompliance = snippetComplianceRepository.findBySnippetIdAndUserId(snippet.id, userData.userId)
          if (existingCompliance != null) {
            existingCompliance.complianceStatus = "unknown"
            snippetComplianceRepository.save(existingCompliance)
          } else {
            val newCompliance = SnippetConformance(
              snippetId = snippet.id,
              userId = userData.userId,
              complianceStatus = "unknown"
            )
            snippetComplianceRepository.save(newCompliance)
          }
        }
      }
    }

    snippets.forEach { snippet ->
      CoroutineScope(Dispatchers.IO).launch {
        val requestId = UUID.randomUUID().toString()
        val lintRequest = LintRequest(requestId, userData.username, snippet.content, snippet.language)
        lintRequestProducer.publishEvent(lintRequest)

        try {
          val complianceResult = lintResponseConsumer.getLintResponse(requestId).await()

          withContext(Dispatchers.IO) {
            val existingCompliance = snippetComplianceRepository.findBySnippetIdAndUserId(snippet.id, userData.userId)
            if (existingCompliance != null) {
              existingCompliance.complianceStatus = complianceResult
              snippetComplianceRepository.save(existingCompliance)
            } else {
              val newCompliance = SnippetConformance(
                snippetId = snippet.id,
                userId = userData.userId,
                complianceStatus = complianceResult
              )
              snippetComplianceRepository.save(newCompliance)
            }
          }
        } catch (e: Exception) {
          logger.error("Error linting snippet ${snippet.id}: ${e.message}")
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
        formatRequestProducer.publishEvent(formatRequest)
        try {
          val formattedContent = formatResponseConsumer.getFormatResponse(requestId).await()
          snippetService.updateSnippet(userData.userId, snippet.id, formattedContent)
        } catch (e: Exception) {
          logger.error("Error formatting snippet ${snippet.id}: ${e.message}")
        }
      }
    }
  }

  private fun getWritableSnippets(userId: String): List<SnippetWithContent> {
    val snippetIds = permissionService.getUserSnippetsOfType(userId, "write")
    return getSnippetContent(snippetIds)
  }

  private fun getSnippetContent(snippetIds: List<String>): List<SnippetWithContent> {
    return snippetIds.mapNotNull { snippetId ->
      try {
        val snippet = snippetService.getSnippetById(snippetId)
        val content = assetService.getAssetContent(snippet.author, snippet.id)
        SnippetWithContent(snippet, content)
      } catch (e: Exception) {
        logger.error("Error retrieving snippet with ID $snippetId: ${e.message}")
        null
      }
    }
  }

  private fun getAllSnippets(userId: String): List<SnippetWithContent> {
    val writeSnippets = permissionService.getUserSnippetsOfType(userId, "write")
    val readSnippets = permissionService.getUserSnippetsOfType(userId, "read")
    val snippetIds = writeSnippets + readSnippets
    return getSnippetContent(snippetIds)
  }
}
