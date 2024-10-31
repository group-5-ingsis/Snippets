package com.ingsis.snippets.snippet

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SnippetRoutes(private val snippetService: SnippetService) : SnippetRoutesSpec {

  override fun createSnippet(snippet: SnippetDto): Snippet {
    return snippetService.createSnippet(snippet)
  }

  override fun getSnippet(@PathVariable id: String): ResponseEntity<Snippet> {
    val snippet = snippetService.getSnippet(id)
    return if (snippet != null) {
      ResponseEntity(snippet, HttpStatus.OK)
    } else {
      ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }

  override fun getAllSnippets(): ResponseEntity<Snippet> {
    TODO("Not yet implemented")
  }

  override fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: Snippet): ResponseEntity<Snippet> {
    val snippet = snippetService.updateSnippet(id, updatedSnippet)
    return if (snippet != null) {
      ResponseEntity(snippet, HttpStatus.OK)
    } else {
      ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }

  override fun deleteSnippet(@PathVariable id: String): ResponseEntity<Void> {
    return if (snippetService.deleteSnippet(id)) {
      ResponseEntity(HttpStatus.OK)
    } else {
      ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }

  override fun lintAllSnippets(): ResponseEntity<Snippet> {
    TODO("Not yet implemented")
  }
}
