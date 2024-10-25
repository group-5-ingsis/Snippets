package com.ingsis.snippets.snippet

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SnippetService(private val snippetRepository: SnippetRepository) {

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
    return if (snippetRepository.existsById(id)) {
      snippetRepository.deleteById(id)
      true
    } else {
      false
    }
  }
}
