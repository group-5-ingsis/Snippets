package com.ingsis.snippets.snippet

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
class SnippetController(private val snippetService: SnippetService) {

  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  @PostMapping("/")
  fun createSnippet(
    @RequestBody snippet: SnippetDto,
    @AuthenticationPrincipal jwt: Jwt
  ): Snippet {
    val userId = jwt.subject
    logger.info("Creating snippet for userId: $userId")
    return snippetService.createSnippet(userId, snippet)
  }

  @GetMapping("/id/{id}")
  fun getSnippetById(@PathVariable id: String): SnippetWithContent {
    logger.info("Fetching snippet with id: $id")
    return snippetService.getSnippetContent(id)
  }

  @GetMapping("/name/{name}")
  fun getSnippetsByName(@PathVariable name: String): List<Snippet> {
    logger.info("Fetching snippets with name: $name")
    return snippetService.getSnippetsByName(name)
  }

  @GetMapping("/")
  fun getAllSnippets(): List<Snippet> {
    logger.info("Fetching all snippets")
    return snippetService.getSnippets()
  }

  @PutMapping("/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody updatedSnippet: SnippetDto): Snippet {
    logger.info("Updating snippet with id: $id")
    return snippetService.updateSnippet(id, updatedSnippet)
  }

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String) {
    logger.info("Deleting snippet with id: $id")
    snippetService.deleteSnippet(id)
  }
}
