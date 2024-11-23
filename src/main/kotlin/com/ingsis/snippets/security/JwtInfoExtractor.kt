package com.ingsis.snippets.security

import com.ingsis.snippets.user.UserData
import org.springframework.security.oauth2.jwt.Jwt

object JwtInfoExtractor {

  private const val CLAIMS_KEY = "https://snippets/claims/username"

  fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims[CLAIMS_KEY]?.toString() ?: "unknown"
    return Pair(userId, username)
  }

  fun extractUserId(jwt: Jwt): String {
    val userId = jwt.subject
    return userId
  }

  fun createUserData(jwt: Jwt): UserData {
    val userId = jwt.subject
    val username = jwt.claims[CLAIMS_KEY]?.toString() ?: "unknown"
    return UserData(userId, username)
  }
}
