package com.ingsis.snippets.snippet

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/snippets")
class SnippetController(private val snippetService: SnippetService) {

  @PostMapping("/create")
  fun createSnippet(@RequestBody snippet: Snippet): ResponseEntity<Snippet> {
    val createdSnippet = snippetService.createSnippet(snippet)
    return ResponseEntity(createdSnippet, HttpStatus.CREATED)
  }

  @GetMapping("/{id}")
  fun getSnippet(@PathVariable id: String): ResponseEntity<Snippet> {
    val snippet = snippetService.getSnippet(id)
    return if (snippet != null) {
      ResponseEntity(snippet, HttpStatus.OK)
    } else {
      ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }

  @PutMapping("/update/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: Snippet): ResponseEntity<Snippet> {
    val snippet = snippetService.updateSnippet(id, updatedSnippet)
    return if (snippet != null) {
      ResponseEntity(snippet, HttpStatus.OK)
    } else {
      ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }

  @DeleteMapping("/delete/{id}")
  fun deleteSnippet(@PathVariable id: String): ResponseEntity<Void> {
    return if (snippetService.deleteSnippet(id)) {
      ResponseEntity(HttpStatus.NO_CONTENT)
    } else {
      ResponseEntity(HttpStatus.NOT_FOUND)
    }
  }
}
