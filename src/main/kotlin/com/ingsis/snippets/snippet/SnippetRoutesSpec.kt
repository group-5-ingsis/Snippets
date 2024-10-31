package com.ingsis.snippets.snippet

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/v1/snippets")
interface SnippetRoutesSpec {

  @PostMapping("/")
  fun createSnippet(
    @RequestBody snippet: SnippetDto
  ): Snippet

  @GetMapping("/{id}")
  fun getSnippet(@PathVariable id: String): Snippet

  @GetMapping("/")
  fun getAllSnippets(): ResponseEntity<Snippet>

  @PutMapping("/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: Snippet): ResponseEntity<Snippet>

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String): ResponseEntity<Void>

  @PostMapping("/lint")
  fun lintAllSnippets(): ResponseEntity<Snippet>
}
