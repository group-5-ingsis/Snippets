package com.ingsis.snippets.user

import com.ingsis.snippets.snippet.SnippetController
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class PermissionService(private val restTemplate: RestTemplate) {

  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  private val permissionServiceUrl: String = System.getenv("PERMISSION_SERVICE_URL") ?: "http://localhost:8083"

  fun updatePermissions(userId: String, snippetId: String, type: String) {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$type/$userId/$snippetId"
    logger.info("Updating permissions for user: $userId, type: $type")

    try {
      val entity = HttpEntity<Void>(headers)

      restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        Void::class.java
      )
      logger.info("Permissions updated successfully.")
    } catch (e: RestClientException) {
      logger.error("Error updating permissions: ${e.message}")
    }
  }

  fun shareSnippet(userId: String, snippetId: String) {
    logger.info("Permission URL: $permissionServiceUrl")

    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/read/$userId/$snippetId"
    logger.info("Sharing snippet: $snippetId to user: $userId")

    val entity = HttpEntity<Void>(headers)

    try {
      restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        Void::class.java
      )
      logger.info("Shared snippet successfully.")
    } catch (e: RestClientException) {
      logger.error("Error sharing snippet: ${e.message}")
    }
  }

  fun getSnippets(userId: String, type: String): List<String> {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$type/$userId"
    logger.info("Getting snippets for user $userId, type: $type")

    return try {
      val entity = HttpEntity<Void>(headers)

      val result = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        object : ParameterizedTypeReference<List<String>>() {}
      )
      result.body ?: emptyList()
    } catch (e: RestClientException) {
      logger.error("Error fetching snippets: ${e.message}")
      emptyList()
    }
  }

  fun getMySnippetsIds(userId: String): List<String> {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$userId"
    logger.info("Getting all of snippets for user: $userId")

    return try {
      val entity = HttpEntity<Void>(headers)

      val result = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        object : ParameterizedTypeReference<List<String>>() {}
      )

      result.body ?: emptyList()
    } catch (e: RestClientException) {
      println("Error fetching snippets: ${e.message}")
      emptyList()
    }
  }

  fun deleteSnippet(snippetId: String, userId: String) {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/delete/$userId/$snippetId"

    try {
      val entity = HttpEntity<Void>(headers)

      restTemplate.exchange(
        url,
        HttpMethod.DELETE,
        entity,
        Void::class.java
      )
    } catch (e: RestClientException) {
      logger.error("Error deleting snippet: ${e.message}")
    }
  }
}
