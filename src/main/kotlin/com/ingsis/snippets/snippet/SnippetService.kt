package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import org.springframework.stereotype.Service

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetService: AssetService
) {

  fun createSnippet(snippetDto: SnippetDto): Snippet {
    val snippet = Snippet(snippetDto)

    val savedSnippet = snippetRepository.save(snippet)

    val asset = Asset(
      container = savedSnippet.author,
      key = savedSnippet.id,
      content = snippetDto.content
    )

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  fun getSnippet(id: String): Snippet {
    return snippetRepository.findById(id).orElse(null)
  }

  fun getSnippetContent(id: String): String {
    val snippet = getSnippet(id)
    val container = snippet.author
    val key = snippet.id

    return assetService.getAssetContent(container, key)
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

  // Falta borrarlo del asset service
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
