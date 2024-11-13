package com.ingsis.snippets.snippet

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class SnippetPermissionService(private val restTemplate: RestTemplate) {

  private val permissionServiceUrl: String = System.getenv("PERMISSION_SERVICE_URL") ?: "permission"

  fun updatePermissions(userId: String, snippetId: String, type: String) {
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
}
