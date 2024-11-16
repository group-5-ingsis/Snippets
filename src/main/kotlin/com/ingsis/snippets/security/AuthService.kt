package com.ingsis.snippets.security

import com.ingsis.snippets.user.Auth0User
import com.ingsis.snippets.user.UserController
import com.ingsis.snippets.user.UserData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class AuthService(
  private val restTemplate: RestTemplate,
  @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val domain: String,
  @Value("\${spring.security.oauth2.resourceserver.jwt.m2m-clientId}") private val clientId: String,
  @Value("\${spring.security.oauth2.resourceserver.jwt.m2m-clientsecret}") private val clientSecret: String
) {

  private val logger = LoggerFactory.getLogger(UserController::class.java)

  fun getAuth0Users(userData: UserData): List<Auth0User> {
    val currentUserId = userData.userId
    val token = getManagementApiToken()
    val url = "${domain}api/v2/users"
    val headers = HttpHeaders().apply {
      set("Authorization", "Bearer $token")
      accept = listOf(MediaType.APPLICATION_JSON)
    }
    val entity = HttpEntity<String>(headers)

    return try {
      val response: ResponseEntity<Array<Auth0User>> = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        Array<Auth0User>::class.java
      )
      response.body?.filter { it.id != currentUserId } ?: emptyList()
    } catch (e: RestClientException) {
      logger.error("Error fetching users: ${e.message}")
      emptyList()
    }
  }

  private fun getManagementApiToken(): String {
    val url = "${domain}oauth/token"
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_FORM_URLENCODED
    }

    val body = LinkedMultiValueMap<String, String>().apply {
      add("client_id", clientId)
      add("client_secret", clientSecret)
      add("audience", "${domain}api/v2/")
      add("grant_type", "client_credentials")
    }

    val entity = HttpEntity(body, headers)

    return try {
      val response: ResponseEntity<Map<String, String>> = restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        object : ParameterizedTypeReference<Map<String, String>>() {}
      )
      response.body?.get("access_token")
        ?: throw IllegalStateException("No access token returned in response")
    } catch (e: RestClientException) {
      println("Error retrieving token: ${e.message}")
      throw IllegalStateException("Failed to retrieve access token")
    }
  }
}
