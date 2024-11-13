package com.ingsis.snippets.snippet

import com.ingsis.snippets.logging.CorrelationIdFilter
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
class SnippetController(private val snippetService: SnippetService, private val permissionService: PermissionService) {

  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  @PostMapping("/")
  fun createSnippet(
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
  fun getSnippetsByName(@PathVariable name: String, @AuthenticationPrincipal jwt: Jwt): List<Snippet> {
    logger.info("Fetching snippets with name: $name (get/name/{name})")
    return snippetService.getSnippetsByName(jwt, name)
  }

  @GetMapping("/")
  fun getAllSnippets(): List<Snippet> {
    logger.info("Fetching all snippets (get/)")
    logger.info("Correlation ID in request: ${MDC.get(CorrelationIdFilter.CORRELATION_ID_HEADER)}")
    return snippetService.getSnippets()
  }

  @PutMapping("/{id}")
  fun updateSnippet(@PathVariable id: String, @RequestBody newSnippetContent: String): SnippetWithContent {
    logger.info("Updating snippet with id: $id (put/{id})")
    return snippetService.updateSnippet(id, newSnippetContent)
  }

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String): String {
    logger.info("Deleting snippet with id: $id (delete/{id})")
    snippetService.deleteSnippet(id)
    return "Snippet deleted!"
  }

  @GetMapping("/users")
  fun getUsers(): List<UserDto> {
    return permissionService.getUsers()
  }

  @PostMapping("/share/{snippetId}/{userToShare}")
  fun shareSnippetWithUser(@PathVariable snippetId: String, @PathVariable userToShare: String, @AuthenticationPrincipal jwt: Jwt) {
    snippetService.shareSnippet(jwt, snippetId, userToShare)
  }

  private fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims["https://snippets/claims/username"]?.toString() ?: "unknown"
    return Pair(userId, username)
  }
}
