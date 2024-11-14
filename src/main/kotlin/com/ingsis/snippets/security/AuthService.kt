package com.ingsis.snippets.security

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

  fun getManagementApiToken(): String {
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
