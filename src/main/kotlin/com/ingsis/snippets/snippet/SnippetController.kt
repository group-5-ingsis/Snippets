package com.ingsis.snippets.snippet

import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/snippet")
@RestController
class SnippetController(private val snippetService: SnippetService) {

  @PostMapping("/")
  fun createSnippet(
    @RequestBody snippet: SnippetDto
  ): Snippet {
    return snippetService.createSnippet(snippet)
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
