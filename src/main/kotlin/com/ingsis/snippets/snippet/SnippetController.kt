package com.ingsis.snippets.snippet

import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.async.producer.format.SnippetFormatRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/v1/snippet")
@RestController
class SnippetController(
  private val snippetService: SnippetService,
  private val snippetFormatProducer: SnippetFormatProducer
) {

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

  @GetMapping("/")
  fun getAllSnippets(): ResponseEntity<Snippet> {
    TODO("Not yet implemented")
  }

  @PutMapping("/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: SnippetDto): Snippet {
    return snippetService.updateSnippet(id, updatedSnippet)
  }

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String) {
    snippetService.deleteSnippet(id)
  }

  @PostMapping("/lint")
  fun lintAllSnippets(): ResponseEntity<Snippet> {
    TODO("Not yet implemented")
  }

  @PostMapping("/format/{id}")
  suspend fun formatSnippet(@PathVariable id: String) {
    val snippet = getSnippet(id)

    val snippetToFormat = SnippetFormatRequest(
      container = snippet.author,
      key = snippet.id,
      language = snippet.language,
      version = snippet.version
    )

    snippetFormatProducer.publishEvent(snippetToFormat)
  }
}
