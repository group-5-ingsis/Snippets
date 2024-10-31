package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetClient
import org.springframework.stereotype.Service

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetClient: AssetClient
) {

  fun createSnippet(snippetDto: SnippetDto): Snippet {
    val snippet = Snippet(snippetDto)

    val asset = Asset(
      container = snippet.author,
      key = snippet.name,
      content = snippetDto.content
    )

    assetClient.createOrUpdateSnippet(asset)

    return snippetRepository.save(snippet)
  }

  fun getSnippet(id: String): Snippet {
    // val content = assetClient.getAsset(id)
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
