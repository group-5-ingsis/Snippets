package com.ingsis.snippets.snippet

import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class PermissionService(private val restTemplate: RestTemplate) {

  private val logger = LoggerFactory.getLogger(PermissionService::class.java)
  private val permissionServiceUrl: String = System.getenv("PERMISSION_SERVICE_URL") ?: "http://permission"

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
