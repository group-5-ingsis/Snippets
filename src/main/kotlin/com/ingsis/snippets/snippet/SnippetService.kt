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
    val snippet = Snippet(snippetDto).apply {
      author = userData.username
    }

    val savedSnippet = withContext(Dispatchers.IO) {
      snippetRepository.save(snippet)
    }

    val snippetConformance = withContext(Dispatchers.Default) {
      val complianceStatus = lintSnippet(userData.username, snippetDto.content, snippetDto.language)

      SnippetConformance(
        snippetId = savedSnippet.id,
        userId = userData.userId,
        complianceStatus = complianceStatus
      )
    }

    withContext(Dispatchers.IO) {
      snippetComplianceRepository.save(snippetConformance)
    }

    createAsset(userData.username, savedSnippet.id, snippetDto.content)
    updatePermissionsForSnippet(userData.userId, savedSnippet.id)

    return savedSnippet
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

    val snippets = if (name.isBlank()) {
      snippetRepository.findAll()
    } else {
      listOf(snippetRepository.findByName(name))
    }

    return snippets.filter { it.id in mySnippetIds }.map { snippet ->
      val complianceStatus = snippetComplianceRepository.findBySnippetIdAndUserId(snippet.id, userId)?.complianceStatus ?: "pending"
      SnippetWithCompliance(snippet, complianceStatus)
    }
  }

  suspend fun updateSnippet(userId: String, snippetId: String, newContent: String): SnippetWithContent {
    val hasWritePermission = permissionService.hasPermissions("write", userId, snippetId)
    if (!hasWritePermission) {
      return SnippetWithContent(getSnippetById(snippetId), "You don't have permission to update this snippet")
    }

    val snippet = getSnippetById(snippetId).apply {
      createAsset(author, id, newContent)
    }

    val updatedSnippet = withContext(Dispatchers.IO) {
      snippetRepository.save(snippet)
    }

    val snippetConformance = withContext(Dispatchers.IO) {
      val complianceResult = lintSnippet(userId, newContent, snippet.language)

      val existingCompliance = snippetComplianceRepository.findBySnippetIdAndUserId(snippetId, userId)
      existingCompliance?.apply {
        complianceStatus = complianceResult
      }
        ?: SnippetConformance(
          snippetId = updatedSnippet.id,
          userId = userId,
          complianceStatus = complianceResult
        )
    }

    withContext(Dispatchers.IO) {
      snippetComplianceRepository.save(snippetConformance)
    }

    withContext(Dispatchers.Default) {
      testService.runAllTests(snippetId)
    }

    return SnippetWithContent(updatedSnippet, newContent)
  }

  fun deleteSnippet(snippetId: String, userId: String): String {
    val snippet = getSnippetById(snippetId)

    val hasWritePermission = permissionService.hasPermissions("write", snippetId, userId)

    if (!hasWritePermission) {
      permissionService.updatePermissions("read", "remove", userId, snippetId)
      return "Read permission removed"
    }

    assetService.deleteAsset(snippet.author, snippet.id)
    snippetRepository.deleteById(snippetId)
    permissionService.deleteSnippet(snippetId)
    return "Deleted snippet"
  }

  fun shareSnippet(snippetId: String, userToShare: String): SnippetWithContent {
    val snippet = getSnippetById(snippetId)
    permissionService.updatePermissions("read", "add", userToShare, snippetId)
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    return SnippetWithContent(snippet, content)
  }

  private fun createAsset(container: String, key: String, content: String) {
    val asset = Asset(container = container, key = key, content = content)
    assetService.createOrUpdateAsset(asset)
  }

  private fun updatePermissionsForSnippet(userId: String, snippetId: String) {
    listOf("read", "write").forEach { permission ->
      permissionService.updatePermissions(permission, "add", userId, snippetId)
    }
  }

  private suspend fun lintSnippet(username: String, content: String, language: String): String {
    val requestId = UUID.randomUUID().toString()
    val lintRequest = LintRequest(requestId, username, content, language)
    lintRequestProducer.publishEvent(lintRequest)

    return try {
      withTimeout(5000L) {
        val responseDeferred = lintResponseConsumer.getLintResponse(requestId)
        responseDeferred.await()
      }
    } catch (e: TimeoutCancellationException) {
      logger.warn("Linting timed out for requestId: $requestId, assuming compliant")
      "compliant"
    }
  }
}
