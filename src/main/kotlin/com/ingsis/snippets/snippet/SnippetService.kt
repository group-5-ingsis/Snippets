package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.lint.LintRequest
import com.ingsis.snippets.async.lint.LintRequestProducer
import com.ingsis.snippets.async.lint.LintResponseConsumer
import com.ingsis.snippets.user.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

  suspend fun createSnippet(userData: UserData, snippetDto: SnippetDto): Snippet {
    val compliance = lintSnippet(userData.username, snippetDto.content)

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
    val snippets = if (name.isBlank()) snippetRepository.findAll() else snippetRepository.findByName(name)
    return snippets.filter { it.id in mySnippetIds }
  }

  fun updateSnippet(userId: String, snippetId: String, newContent: String): SnippetWithContent {
    permissionService.hasPermission(userId, snippetId)
    val snippet = getSnippetById(snippetId)
    createAsset(snippet.author, snippet.id, newContent)
    return SnippetWithContent(snippet, newContent)
  }

  fun deleteSnippet(id: String) {
    val snippet = getSnippetById(id)
    assetService.deleteAsset(snippet.author, snippet.id)
    snippetRepository.deleteById(id)
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

  private suspend fun lintSnippet(username: String, content: String): String {
    val requestId = UUID.randomUUID().toString()
    val lintRequest = LintRequest(requestId, username, snippet = content)
    lintRequestProducer.publishEvent(lintRequest)
    val responseDeferred = lintResponseConsumer.getLintResponseResponse(requestId)
    return responseDeferred.await()
  }
}
