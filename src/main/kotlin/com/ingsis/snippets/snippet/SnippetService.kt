package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.LintRequest
import com.ingsis.snippets.async.lint.LintRequestProducer
import com.ingsis.snippets.async.lint.LintResponseConsumer
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
  private val lintResponseConsumer: LintResponseConsumer
) {

  private val logger = LoggerFactory.getLogger(SnippetService::class.java)

  suspend fun createSnippet(userData: UserData, snippetDto: SnippetDto): Snippet {
    val compliance = lintSnippet(userData.username, snippetDto.content, snippetDto.language)

    val snippet = Snippet(snippetDto, compliance).apply {
      author = userData.username
    }

    val savedSnippet = withContext(Dispatchers.IO) {
      snippetRepository.save(snippet)
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

  fun getSnippetsByName(userId: String, name: String): List<Snippet> {
    val mySnippetIds = permissionService.getMySnippetsIds(userId)

    val snippets = if (name.isBlank()) {
      snippetRepository.findAll()
    } else {
      listOf(snippetRepository.findByName(name))
    }

    return snippets.filter { it.id in mySnippetIds }
  }

  suspend fun updateSnippet(userId: String, snippetId: String, newContent: String): SnippetWithContent {
    val writePermissionSnippets = permissionService.getSnippets(userId, "write")
    logger.info("snippets with write permission: $writePermissionSnippets, snippetId: $snippetId")
    if (snippetId !in writePermissionSnippets) {
      return SnippetWithContent(getSnippetById(snippetId), "You don't have permission to update this snippet")
    }
    val snippet = getSnippetById(snippetId)
    val compliance = lintSnippet(snippet.author, newContent, snippet.language)
    snippet.compliance = compliance

    val savedSnippet = withContext(Dispatchers.IO) {
      snippetRepository.save(snippet)
    }

    createAsset(savedSnippet.author, savedSnippet.id, newContent)
    return SnippetWithContent(savedSnippet, newContent)
  }

  fun deleteSnippet(snippetId: String, userId: String): String {
    val snippet = getSnippetById(snippetId)
    val writePermissionSnippets = permissionService.getSnippets(userId, "write")
    if (snippet.id !in writePermissionSnippets) {
      permissionService.removePermission(snippetId, userId, "read")
      return "You don't have permission to delete this snippet"
    }
    assetService.deleteAsset(snippet.author, snippet.id)
    snippetRepository.deleteById(snippetId)
    permissionService.deleteSnippet(snippetId)
    return "Snippet deleted!"
  }

  fun shareSnippet(userId: String, snippetId: String, userToShare: String): SnippetWithContent {
    val snippet = getSnippetById(snippetId)
    if (hasWritePermission(userId, snippetId)) {
      permissionService.shareSnippet(userToShare, snippetId)
    }
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    return SnippetWithContent(snippet, content)
  }

  private fun createAsset(container: String, key: String, content: String) {
    val asset = Asset(container = container, key = key, content = content)
    assetService.createOrUpdateAsset(asset)
  }

  private fun updatePermissionsForSnippet(userId: String, snippetId: String) {
    listOf("read", "write").forEach { permission ->
      permissionService.updatePermissions(userId, snippetId, permission)
    }
  }

  private fun hasWritePermission(userId: String, snippetId: String): Boolean {
    val writableSnippets = permissionService.getSnippets(userId, "write")
    return snippetId in writableSnippets
  }

  private suspend fun lintSnippet(username: String, content: String, language: String): String {
    val parts = getLanguageData(language)

    val languageName = parts[0]
    val version = parts[1]

    val requestId = UUID.randomUUID().toString()
    val lintRequest = LintRequest(requestId, username, content, languageName, version)
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

  private fun getLanguageData(language: String): List<String> {
    val parts = language.split(" ", limit = 2)
    if (parts.size != 2) {
      throw IllegalArgumentException("Invalid language format. Expected format: 'LanguageName version'. Got: $language")
    }
    return parts
  }
}
