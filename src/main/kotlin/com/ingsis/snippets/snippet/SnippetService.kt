package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.AssetClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetClient: AssetClient
) {

  fun createSnippet(snippet: SnippetDto): String {
    return assetClient.createOrUpdateSnippet(snippet.container, snippet.key, snippet)
  }

  fun getSnippet(id: String): Snippet? {
    return snippetRepository.findById(id).orElse(null)
  }

  fun updateSnippet(id: String, updatedSnippet: Snippet): Snippet? {
    val existingSnippet = snippetRepository.findById(id).orElse(null)
    return if (existingSnippet != null) {
      existingSnippet.language = updatedSnippet.language
      existingSnippet.modificationDate = LocalDateTime.now()
      existingSnippet.comments = updatedSnippet.comments
      existingSnippet.testCases = updatedSnippet.testCases

      snippetRepository.save(existingSnippet)
    } else {
      null
    }
  }

  fun deleteSnippet(id: String): Boolean {
    val snippetExists = snippetRepository.existsById(id)
    return if (snippetExists) {
      snippetRepository.deleteById(id)
      true
    } else {
      false
    }
  }
}
