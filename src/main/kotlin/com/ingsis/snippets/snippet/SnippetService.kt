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

  fun updateSnippet(id: String, newSnippet: SnippetDto): Snippet {
    val existingSnippet = getSnippet(id)

    val updatedSnippet = updateFields(existingSnippet, newSnippet)

    val savedSnippet = snippetRepository.save(updatedSnippet)

    val asset = Asset(
      container = savedSnippet.author,
      key = savedSnippet.id,
      content = newSnippet.content
    )

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  private fun updateFields(existingSnippet: Snippet, updatedSnippet: SnippetDto): Snippet {
    return Snippet(
      id = existingSnippet.id,
      author = existingSnippet.author,
      description = updatedSnippet.description,
      name = updatedSnippet.name,
      version = updatedSnippet.version,
      language = updatedSnippet.language,
      compliant = "unknown"
    )
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
