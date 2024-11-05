package com.ingsis.snippets.snippet

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SnippetRoutes(private val snippetService: SnippetService) : SnippetRoutesSpec {

  override fun createSnippet(snippet: SnippetDto): Snippet {
    return snippetService.createSnippet(snippet)
  }

  override fun getSnippet(id: String): Snippet {
    return snippetService.getSnippet(id)
  }

  override fun getSnippetContent(id: String): String {
    return snippetService.getSnippetContent(id)
  }

  override fun getAllSnippets(): ResponseEntity<Snippet> {
    TODO("Not yet implemented")
  }

  override fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: SnippetDto): Snippet {
    return snippetService.updateSnippet(id, updatedSnippet)
  }

  override fun deleteSnippet(@PathVariable id: String) {
    snippetService.deleteSnippet(id)
  }

  override fun lintAllSnippets(): ResponseEntity<Snippet> {
    TODO("Not yet implemented")
  }
}
