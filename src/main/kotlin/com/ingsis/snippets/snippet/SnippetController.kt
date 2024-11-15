package com.ingsis.snippets.snippet

import com.ingsis.snippets.security.JwtInfoExtractor
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
    val userData = JwtInfoExtractor.createUserData(jwt)
    logger.info("Creating snippet for user: ${userData.username}")
    return snippetService.createSnippet(userData, snippet)
  }

  @GetMapping("/id/{id}")
  fun getSnippetById(@PathVariable id: String): SnippetWithContent {
    logger.info("Fetching snippet with id: $id")
    return snippetService.getSnippetContent(id)
  }

  @GetMapping("/name/{snippetName}")
  fun getSnippetsByName(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetName: String): List<Snippet> {
    val (userId, _) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Fetching snippets with name: $snippetName (get/name/{name})")
    return snippetService.getSnippetsByName(userId, snippetName)
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

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String): String {
    logger.info("Deleting snippet with id: $id (delete/{id})")
    snippetService.deleteSnippet(id)
    return "Snippet deleted!"
  }
}
