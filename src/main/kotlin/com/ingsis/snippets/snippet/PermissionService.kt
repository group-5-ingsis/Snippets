package com.ingsis.snippets.snippet

import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class PermissionService(private val restTemplate: RestTemplate) {

  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  private val permissionServiceUrl: String = System.getenv("PERMISSION_SERVICE_URL")

  fun updatePermissions(userId: String, snippetId: String, type: String) {

    logger.info("Permission URL: $permissionServiceUrl")

    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }
    val url = "$permissionServiceUrl/$type/$userId/$snippetId"
    try {
      restTemplate.exchange(url, HttpMethod.POST, HttpEntity<Unit>(null, headers), Void::class.java)
    } catch (_: RestClientException) {
      "Error updating permissions"
    }
  }

  fun getSnippets(userData: UserData, type: String): List<String> {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val requestEntity = HttpEntity(userData, headers)

    val url = "$permissionServiceUrl/$type"

    return try {
      val result = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        List::class.java
      )
      result.body as List<String>
    } catch (_: RestClientException) {
      emptyList()
    }
  }

  fun getUsers(): List<UserDto> {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/users"

    try {
      val result = restTemplate.exchange(url, HttpMethod.GET, HttpEntity<Unit>(null, headers), List::class.java)
      return result.body as List<UserDto>
    } catch (_: RestClientException) {
      return emptyList()
    }
  }

  fun getMySnippetsIds(token: Jwt): List<String> {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/"

    return try {
      val result = restTemplate.exchange(
        url,
        HttpMethod.POST,
        HttpEntity<Unit>(null, headers),
        object : ParameterizedTypeReference<List<String>>() {}
      )

      result.body ?: emptyList()
    } catch (e: RestClientException) {
      println("Error fetching snippets: ${e.message}")
      emptyList()
    }
  }
}
