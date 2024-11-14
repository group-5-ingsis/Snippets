package com.ingsis.snippets.asset

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate) {

  // Set the default to an absolute URL
  private val assetServiceBaseUrl: String = System.getenv("ASSET_SERVICE_URL") ?: "http://asset_service"

  fun getAssetContent(container: String, key: String): String {
    val headers = createHeaders(MediaType.TEXT_EVENT_STREAM)
    val url = buildUrl(container, key)

    return try {
      val response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity<Unit>(null, headers), String::class.java)
      response.body ?: "No Content"
    } catch (_: RestClientException) {
      "No Content"
    }
  }

  fun createOrUpdateAsset(asset: Asset): String {
    val headers = createHeaders(MediaType.APPLICATION_JSON)
    val url = buildUrl(asset.container, asset.key)

    return try {
      val response = restTemplate.exchange(url, HttpMethod.PUT, HttpEntity(asset.content, headers), String::class.java)
      response.body ?: "No Content"
    } catch (e: RestClientException) {
      handleException(e, "Error creating or updating asset")
    }
  }

  fun deleteAsset(container: String, key: String) {
    val headers = createHeaders()
    val url = buildUrl(container, key)

    try {
      restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity<Unit>(headers), Void::class.java)
    } catch (e: RestClientException) {
      handleException(e, "Error deleting asset")
    }
  }

  private fun createHeaders(contentType: MediaType? = null): HttpHeaders {
    return HttpHeaders().apply {
      contentType?.let { this.contentType = it }
      accept = listOf(MediaType.ALL)
    }
  }

  private fun buildUrl(container: String, key: String): String {
    return "$assetServiceBaseUrl/$container/$key"
  }

  private fun handleException(e: RestClientException, defaultMessage: String): String {
    return "$defaultMessage: ${e.message ?: "Unknown error"}"
  }
}
