package com.ingsis.snippets.snippet

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
class SnippetController(private val snippetService: SnippetService) {

  @PostMapping("/")
  fun createSnippet(
    @RequestBody snippet: SnippetDto,
    @AuthenticationPrincipal jwt: Jwt
  ): Snippet {
    val userId = jwt.subject
    return snippetService.createSnippet(userId, snippet)
  }

  @GetMapping("/{id}")
  fun getSnippet(@PathVariable id: String): Snippet {
    return snippetService.getSnippet(id)
  }

  @GetMapping("/{id}/content")
  fun getSnippetContent(@PathVariable id: String): String {
    return snippetService.getSnippetContent(id)
  }

  @PutMapping("/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: SnippetDto): Snippet {
    return snippetService.updateSnippet(id, updatedSnippet)
  }

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String) {
    snippetService.deleteSnippet(id)
  }
}
