package com.ingsis.snippets.snippet

import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
class SnippetController(private val snippetService: SnippetService) {

  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  @PostMapping("/")
  suspend fun createSnippet(
    @RequestBody snippet: SnippetDto,
    @AuthenticationPrincipal jwt: Jwt
  ): Snippet {
    val (userId, username) = extractUserInfo(jwt)
    logger.info("Creating snippet for user: $username")
    return snippetService.createSnippet(userId, username, snippet)
  }

  @GetMapping("/id/{id}")
  fun getSnippetById(@PathVariable id: String): SnippetWithContent {
    logger.info("Fetching snippet with id: $id")
    return snippetService.getSnippetContent(id)
  }

  @GetMapping("/name/{name}")
  fun getSnippetsByName(@AuthenticationPrincipal jwt: Jwt, @PathVariable name: String): List<Snippet> {
    val (userId, _) = extractUserInfo(jwt)
    logger.info("Fetching snippets with name: $name (get/name/{name})")
    return snippetService.getSnippetsByName(userId, name)
  }

  @GetMapping("/")
  fun getAllSnippets(): List<Snippet> {
    logger.info("Fetching all snippets (get/)")
    return snippetService.getSnippets()
  }

  @PutMapping("/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody newSnippetContent: String): SnippetWithContent {
    logger.info("Updating snippet with id: $id (put/{id})")
    return snippetService.updateSnippet(id, newSnippetContent)
  }

  @DeleteMapping("/{snippetId}")
  fun deleteSnippet(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String): String {
    val (userId, _) = extractUserInfo(jwt)
    logger.info("Deleting snippet with id: $snippetId (delete/{id})")
    snippetService.deleteSnippet(snippetId, userId)
    return "Snippet deleted!"
  }

  @PostMapping("/share/{snippetId}/{userToShare}")
  fun shareSnippetWithUser(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String, @PathVariable userToShare: String): SnippetWithContent {
    val (userId, _) = extractUserInfo(jwt)
    return snippetService.shareSnippet(userId, snippetId, userToShare)
  }

  private fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims["https://snippets/claims/username"]?.toString() ?: "unknown"
    return Pair(userId, username)
  }
}
