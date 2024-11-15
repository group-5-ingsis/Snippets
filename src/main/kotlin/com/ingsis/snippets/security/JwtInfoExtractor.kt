package com.ingsis.snippets.security

import org.springframework.security.oauth2.jwt.Jwt

object JwtInfoExtractor {

  fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims["https://snippets/claims/username"]?.toString() ?: "unknown"
    return Pair(userId, username)
  }
}
