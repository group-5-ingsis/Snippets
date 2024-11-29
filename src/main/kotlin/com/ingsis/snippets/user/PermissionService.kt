package com.ingsis.snippets.user

import com.ingsis.snippets.snippet.SnippetController
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class PermissionService(private val restTemplate: RestTemplate) {

  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  private val permissionServiceUrl: String = System.getenv("PERMISSION_SERVICE_URL") ?: "http://localhost:8083"

  fun updatePermissions(type: String, operation: String, userId: String, snippetId: String) {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$type/$operation/$userId/$snippetId"
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

  fun deleteSnippet(snippetId: String) {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$snippetId"
    logger.info("Deleting snippet: $snippetId")

    try {
      val entity = HttpEntity<Void>(headers)

      restTemplate.exchange(
        url,
        HttpMethod.DELETE,
        entity,
        Void::class.java
      )
      logger.info("Snippet deleted successfully.")
    } catch (e: RestClientException) {
      logger.error("Error deleting snippet: ${e.message}")
    }
  }

  fun hasPermissions(type: String, userId: String, snippetId: String): Boolean {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$type/$snippetId/$userId"
    logger.info("Checking if user has $type permissions for snippetId: $snippetId")

    return try {
      val entity = HttpEntity<Void>(headers)

      val result = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        Boolean::class.java
      )
      result.body ?: false
    } catch (e: RestClientException) {
      logger.error("Error fetching permissions for userId $userId and snippetId $snippetId: ${e.message}")
      false
    }
  }

  fun getUserSnippetsOfType(userId: String, type: String): List<String> {
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
}
