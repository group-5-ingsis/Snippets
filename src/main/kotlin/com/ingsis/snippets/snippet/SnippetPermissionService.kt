package com.ingsis.snippets.snippet

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.slf4j.LoggerFactory

@Service
class SnippetPermissionService(private val restTemplate: RestTemplate) {

  private val logger = LoggerFactory.getLogger(SnippetPermissionService::class.java)
  private val permissionServiceUrl: String = System.getenv("PERMISSION_SERVICE_URL") ?: "http://default-permission-service-url"

  fun updatePermissions(userId: String, snippetId: String, type: String) {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$permissionServiceUrl/$type/$userId/$snippetId"
    try {
      restTemplate.exchange(url, HttpMethod.POST, HttpEntity<Unit>(null, headers), Void::class.java)
      logger.info("Permissions updated successfully for userId: $userId, snippetId: $snippetId, type: $type")
    } catch (ex: RestClientException) {
      logger.error("Error updating permissions for userId: $userId, snippetId: $snippetId, type: $type", ex)
    }
  }
}
