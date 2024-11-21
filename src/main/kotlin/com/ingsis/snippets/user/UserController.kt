package com.ingsis.snippets.user

import com.ingsis.snippets.security.AuthService
import com.ingsis.snippets.security.JwtInfoExtractor
import com.ingsis.snippets.snippet.SnippetService
import com.ingsis.snippets.snippet.SnippetWithContent
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
  private val snippetService: SnippetService,
  private val authService: AuthService
) {

  private val logger = LoggerFactory.getLogger(UserController::class.java)

  @GetMapping("/users")
  fun getUsers(@AuthenticationPrincipal jwt: Jwt): List<UserDto> {
    val userData = JwtInfoExtractor.createUserData(jwt)
    val auth0Users = authService.getAuth0Users(userData)
    logger.info("Retrieved ${auth0Users.size} users")
    return auth0Users.map { it.toUserDto() }
  }

  @PostMapping("/share/{snippetId}/{userToShare}")
  fun shareSnippetWithUser(@AuthenticationPrincipal jwt: Jwt, @PathVariable snippetId: String, @PathVariable userToShare: String): SnippetWithContent {
    val (userId, _) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Sharing snippet: $snippetId with user: $userToShare")
    return snippetService.shareSnippet(userId, snippetId, userToShare)
  }
}
