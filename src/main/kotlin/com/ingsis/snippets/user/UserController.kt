package com.ingsis.snippets.user

import com.ingsis.snippets.security.AuthService
import com.ingsis.snippets.security.JwtInfoExtractor
import com.ingsis.snippets.snippet.SnippetService
import com.ingsis.snippets.snippet.SnippetWithContent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@RestController
class UserController(
  private val auth0ManagementTokenService: AuthService,
  @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val auth0Domain: String,
  private val snippetService: SnippetService,
  private val restTemplate: RestTemplate
) {

  private val logger = LoggerFactory.getLogger(UserController::class.java)

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

  @PostMapping("/share/{snippetId}/{userToShare}")
  fun shareSnippetWithUser(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String, @PathVariable userToShare: String): SnippetWithContent {
    val (userId, _) = JwtInfoExtractor.extractUserInfo(jwt)
    return snippetService.shareSnippet(userId, snippetId, userToShare)
  }
}
