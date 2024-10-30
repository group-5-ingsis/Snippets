package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetClient
import org.springframework.stereotype.Service

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetClient: AssetClient
) {

  fun createSnippet(snippet: SnippetDto): String {
    // snippetRepository.save(snippet);
    val asset = Asset(
      container = snippet.author,
      key = snippet.name,
      content = snippet.content
    )
    return assetClient.createOrUpdateSnippet(asset)
  }

  fun getSnippet(id: String): Snippet? {
    return snippetRepository.findById(id).orElse(null)
  }

  fun updateSnippet(id: String, updatedSnippet: Snippet): Snippet? {
    val existingSnippet = snippetRepository.findById(id).orElse(null)
    return if (existingSnippet != null) {
      existingSnippet.language = updatedSnippet.language
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
