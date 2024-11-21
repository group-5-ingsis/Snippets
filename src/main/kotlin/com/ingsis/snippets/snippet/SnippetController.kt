package com.ingsis.snippets.snippet

import com.ingsis.snippets.security.JwtInfoExtractor
import com.newrelic.api.agent.NewRelic
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
  ): ResponseEntity<Snippet> {
    val userData = JwtInfoExtractor.createUserData(jwt)
    logger.info("Creating snippet for user: ${userData.username}")

    val snippetResponse = snippetService.createSnippet(userData, snippet)

    return ResponseEntity(snippetResponse, HttpStatus.OK)
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
  fun getAllSnippets(@AuthenticationPrincipal jwt: Jwt): List<Snippet> {
    val (userId, _) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Fetching snippets for user:  $userId (get/)")
    return snippetService.getSnippetsByName(userId, "")
  }

  @PutMapping("/{snippetId}")
  suspend fun updateSnippet(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String, @RequestBody newSnippetContent: String): SnippetWithContent {
    logger.info("Updating snippet with id: $snippetId (put/{id})")
    val (userId, _) = JwtInfoExtractor.extractUserInfo(jwt)
    return snippetService.updateSnippet(userId, snippetId, newSnippetContent)
  }

  @DeleteMapping("/{snippetId}")
  fun deleteSnippet(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String): String {
    val (userId, _) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Deleting snippet with id: $snippetId (delete/{id})")
    return snippetService.deleteSnippet(snippetId, userId)
  }

  @PostMapping("/trigger-alert")
  fun triggerAlert(): ResponseEntity<String> {
    // Log and trigger an alert in New Relic
    val alertMessage = "Manual alert triggered by hitting /trigger-alert endpoint"
    logger.error(alertMessage)

    // Report the custom error to New Relic
    NewRelic.noticeError(RuntimeException(alertMessage))

    NewRelic.addCustomParameter("triggeredBy", "SnippetController")

    return ResponseEntity("Alert triggered and reported to New Relic", HttpStatus.OK)
  }
}
