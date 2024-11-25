package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.LintRequest
import com.ingsis.snippets.async.lint.LintRequestProducer
import com.ingsis.snippets.async.lint.LintResponseConsumer
import com.ingsis.snippets.test.TestService
import com.ingsis.snippets.user.PermissionService
import com.ingsis.snippets.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetService: AssetService,
  private val permissionService: PermissionService,
  private val lintRequestProducer: LintRequestProducer,
  private val lintResponseConsumer: LintResponseConsumer,
  private val testService: TestService,
  private val snippetComplianceRepository: SnippetComplianceRepository
) {

  private val logger = LoggerFactory.getLogger(SnippetService::class.java)

  suspend fun createSnippet(userData: UserData, snippetDto: SnippetDto): Snippet {
    val snippet = createAndSaveSnippet(userData, snippetDto)
    saveSnippetConformance(userData, snippet, snippetDto.content)
    createAsset(userData.username, snippet.id, snippetDto.content)
    grantPermissions(userData.userId, snippet.id, listOf("read", "write"))
    logger.info("Snippet created with ID: ${snippet.id}")
    return snippet
  }

  fun getSnippetById(id: String): Snippet =
    snippetRepository.findById(id).orElseThrow { IllegalArgumentException("Snippet not found") }

  fun getSnippetContent(id: String): SnippetWithContent {
    val snippet = getSnippetById(id)
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    return SnippetWithContent(snippet, content)
  }

  fun getSnippetsByName(userId: String, name: String): List<SnippetWithCompliance> {
    val mySnippetIds = permissionService.getMySnippetsIds(userId)
    return (if (name.isBlank()) snippetRepository.findAll() else listOfNotNull(snippetRepository.findByName(name)))
      .filter { it.id in mySnippetIds }
      .map { snippet ->
        val complianceStatus = snippetComplianceRepository
          .findBySnippetIdAndUserId(snippet.id, userId)?.complianceStatus ?: "pending"
        SnippetWithCompliance(snippet, complianceStatus)
      }
  }

  suspend fun updateSnippet(userId: String, snippetId: String, newContent: String): SnippetWithContent {
    ensureWritePermission(userId, snippetId)
    val snippet = updateSnippetContent(snippetId, newContent)
    val complianceResult = lintSnippet(userId, newContent, snippet.language)
    updateOrCreateCompliance(snippetId, userId, complianceResult)
    testService.runAllTests(snippetId)
    return SnippetWithContent(snippet, newContent)
  }

  fun deleteSnippet(snippetId: String, userId: String): String {
    val snippet = getSnippetById(snippetId)

    if (!permissionService.hasPermissions("write", userId, snippetId)) {
      permissionService.updatePermissions("read", "remove", userId, snippetId)
      return "Read permission removed"
    }

    assetService.deleteAsset(snippet.author, snippet.id)
    snippetRepository.deleteById(snippetId)
    permissionService.deleteSnippet(snippetId)
    logger.info("Snippet with ID: $snippetId deleted successfully")
    return "Deleted snippet"
  }

  fun shareSnippet(snippetId: String, userToShare: String): SnippetWithContent {
    val snippet = getSnippetById(snippetId)
    permissionService.updatePermissions("read", "add", userToShare, snippetId)
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    return SnippetWithContent(snippet, content)
  }

  private suspend fun createAndSaveSnippet(userData: UserData, snippetDto: SnippetDto): Snippet {
    val snippet = Snippet(snippetDto).apply { author = userData.username }
    return withContext(Dispatchers.IO) { snippetRepository.save(snippet) }
  }

  private suspend fun saveSnippetConformance(userData: UserData, snippet: Snippet, content: String) {
    val complianceStatus = lintSnippet(userData.username, content, snippet.language)
    val conformance = SnippetConformance(
      snippetId = snippet.id,
      userId = userData.userId,
      complianceStatus = complianceStatus
    )
    withContext(Dispatchers.IO) { snippetComplianceRepository.save(conformance) }
  }

  private fun createAsset(container: String, key: String, content: String) {
    val asset = Asset(container = container, key = key, content = content)
    assetService.createOrUpdateAsset(asset)
  }

  private fun grantPermissions(userId: String, snippetId: String, permissions: List<String>) {
    permissions.forEach { permission ->
      permissionService.updatePermissions(permission, "add", userId, snippetId)
    }
  }

  private suspend fun lintSnippet(username: String, content: String, language: String): String {
    val requestId = UUID.randomUUID().toString()
    val lintRequest = LintRequest(requestId, username, content, language)
    lintRequestProducer.publishEvent(lintRequest)

    return try {
      withTimeout(5000L) {
        lintResponseConsumer.getLintResponse(requestId).await()
      }
    } catch (e: TimeoutCancellationException) {
      logger.warn("Linting timed out for requestId: $requestId, assuming compliant")
      "compliant"
    }
  }

  private fun ensureWritePermission(userId: String, snippetId: String) {
    if (!permissionService.hasPermissions("write", userId, snippetId)) {
      throw IllegalAccessException("You don't have permission to update this snippet")
    }
  }

  private suspend fun updateSnippetContent(snippetId: String, newContent: String): Snippet {
    val snippet = getSnippetById(snippetId).apply {
      createAsset(author, id, newContent)
    }
    return withContext(Dispatchers.IO) { snippetRepository.save(snippet) }
  }

  private suspend fun updateOrCreateCompliance(snippetId: String, userId: String, complianceResult: String): SnippetConformance {
    val existingCompliance = withContext(Dispatchers.IO) {
      snippetComplianceRepository.findBySnippetIdAndUserId(snippetId, userId)
    }
    return (
      existingCompliance?.apply { complianceStatus = complianceResult }
        ?: SnippetConformance(snippetId = snippetId, userId = userId, complianceStatus = complianceResult)
      ).also {
      withContext(Dispatchers.IO) { snippetComplianceRepository.save(it) }
    }
  }
}
