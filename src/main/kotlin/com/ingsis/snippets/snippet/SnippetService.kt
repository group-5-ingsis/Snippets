package com.ingsis.snippets.snippet

import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val restTemplate: RestTemplate
) {

  private val assetServiceBaseUrl = System.getProperty("ASSET_SERVICE_URL")

  fun createSnippet(snippet: SnippetDto): Boolean {
    val container = snippet.container
    val key = snippet.key

    try {
      val response = restTemplate.postForEntity(
        "$assetServiceBaseUrl/$container/$key",
        snippet,
        Snippet::class.java
      )
      return response.statusCode.is2xxSuccessful
    } catch (e: HttpClientErrorException) {
      println("Error creating snippet: ${e.message}")
      return false
    }
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
