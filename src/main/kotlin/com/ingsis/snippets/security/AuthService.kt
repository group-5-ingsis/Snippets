package com.ingsis.snippets.security

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class AuthService(
  private val restTemplate: RestTemplate,
  @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") private val domain: String,
  @Value("\${spring.security.oauth2.resourceserver.jwt.client-id}") private val clientId: String,
  @Value("\${spring.security.oauth2.resourceserver.jwt.client-secret}") private val clientSecret: String,
  @Value("\${spring.security.oauth2.resourceserver.jwt.management-api}") private val audience: String
) {

  data class Auth0TokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("token_type") val tokenType: String
  )

  fun getManagementApiToken(): String? {
    val url = "https://$domain/oauth/token"
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
    }
    val requestBody = mapOf(
      "client_id" to clientId,
      "client_secret" to clientSecret,
      "audience" to audience,
      "grant_type" to "client_credentials"
    )
    val entity = HttpEntity(requestBody, headers)

    return try {
      val response: Auth0TokenResponse = restTemplate.postForObject(url, entity, Auth0TokenResponse::class.java)!!
      response.accessToken
    } catch (e: RestClientException) {
      null
    }
  }
}
