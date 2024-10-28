package com.ingsis.snippets.asset

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Component
class AssetClient(private val restTemplate: RestTemplate) {

  private val assetServiceBaseUrl = System.getProperty("ASSET_SERVICE_URL")

  fun createOrUpdateSnippet(asset: Asset): String {
    val container = asset.container
    val key = asset.key

    return try {
      val headers = HttpHeaders().apply {
        set("Content-Type", "application/json")
      }

      val requestEntity = HttpEntity(asset.content, headers)

      val response: ResponseEntity<Void> = restTemplate.exchange(
        "$assetServiceBaseUrl/$container/$key",
        HttpMethod.PUT,
        requestEntity,
        Void::class.java
      )

      when (response.statusCode) {
        HttpStatus.CREATED -> {
          "Snippet created successfully."
        }
        HttpStatus.OK -> {
          "Snippet updated successfully."
        }
        else -> {
          "Unexpected response status: ${response.statusCode}"
        }
      }
    } catch (e: HttpClientErrorException) {
      "Error creating or updating snippet: ${e.message}"
    }
  }
}
