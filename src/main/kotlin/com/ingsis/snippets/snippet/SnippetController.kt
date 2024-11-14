package com.ingsis.snippets.snippet

import com.ingsis.snippets.security.AuthService
import com.ingsis.snippets.user.UserDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@RestController
class SnippetController(
  private val snippetService: SnippetService,
  private val auth0ManagementTokenService: AuthService,
  @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val auth0Domain: String,
  private val restTemplate: RestTemplate
) {

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

  @DeleteMapping("/{id}")
  fun deleteSnippet(@PathVariable id: String): String {
    logger.info("Deleting snippet with id: $id (delete/{id})")
    snippetService.deleteSnippet(id)
    return "Snippet deleted!"
  }

  @PostMapping("/share/{snippetId}/{userToShare}")
  fun shareSnippetWithUser(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String, @PathVariable userToShare: String): SnippetWithContent {
    val (userId, _) = extractUserInfo(jwt)
    return snippetService.shareSnippet(userId, snippetId, userToShare)
  }

  @GetMapping("/users")
  fun getUsers(): List<UserDto> {
    val token = auth0ManagementTokenService.getManagementApiToken()
    val url = "${auth0Domain}api/v2/users"
    val headers = HttpHeaders().apply {
      set("Authorization", "Bearer $token")
      accept = listOf(MediaType.APPLICATION_JSON)
    }
    val entity = HttpEntity<String>(headers)

    return try {
      val response: ResponseEntity<Array<UserDto>> = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        Array<UserDto>::class.java
      )
      response.body?.toList() ?: emptyList()
    } catch (e: RestClientException) {
      logger.error("Error fetching users: ${e.message}")
      emptyList()
    }
  }

  private fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims["https://snippets/claims/username"]?.toString() ?: "unknown"
    return Pair(userId, username)
  }
}
