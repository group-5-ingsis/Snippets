package com.ingsis.snippets.asset

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate) {

  private val assetServiceBaseUrl: String = System.getProperty("ASSET_SERVICE_URL") ?: throw IllegalArgumentException("Asset service URL not set")

  private fun createHeaders(): HttpHeaders {
    return HttpHeaders().apply {
      set("Content-Type", "application/json")
    }
  }

  fun getAssetContent(container: String, key: String): String {
    val requestEntity = HttpEntity<String>(null, createHeaders())

    return try {
      val response: ResponseEntity<String> = restTemplate.exchange(
        "$assetServiceBaseUrl/$container/$key",
        HttpMethod.GET,
        requestEntity,
        String::class.java
      )

      return response.body ?: "No Content"
    } catch (e: RestClientException) {
      handleException(e, "Error retrieving asset content")
    }
  }

  fun createOrUpdateAsset(asset: Asset): String {
    val requestEntity = HttpEntity(asset.content, createHeaders())
    val container = asset.container
    val key = asset.key

    return try {
      val response: ResponseEntity<Void> = restTemplate.exchange(
        "$assetServiceBaseUrl/$container/$key",
        HttpMethod.PUT,
        requestEntity,
        Void::class.java
      )

      when (response.statusCode) {
        HttpStatus.CREATED -> "Asset created successfully."
        HttpStatus.OK -> "Asset updated successfully."
        else -> "Unexpected response status: ${response.statusCode}"
      }
    } catch (e: RestClientException) {
      handleException(e, "Error creating or updating asset")
    }
  }

  private fun handleException(e: RestClientException, defaultMessage: String): String {
    return "$defaultMessage: ${e.message ?: "Unknown error"}"
  }
}
