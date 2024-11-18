package com.ingsis.snippets.test

import com.ingsis.snippets.snippet.SnippetController
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class TestService(private val restTemplate: RestTemplate) {
  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  private val testServiceUrl: String = System.getenv("TEST_SERVICE_URL") ?: "http://localhost:8084"

  fun createTest(snippetId: String, testDto: TestDto, authToken: String): TestDto {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
      set("Authorization", "Bearer $authToken")
    }
    logger.info("Content: $testDto")

    val url = "$testServiceUrl/$snippetId"
    logger.info("Sending test creation request to URL: $url")

    try {
      val entity = HttpEntity(testDto, headers)

      val response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        TestDto::class.java
      )

      logger.info("Test creation request sent successfully.")

      return response.body ?: throw RuntimeException("Failed to create test. No response body.")
    } catch (e: RestClientException) {
      logger.error("Error sending test creation request: ${e.message}")
      throw RuntimeException("Error sending test creation request: ${e.message}", e)
    }
  }

  fun runTest(testId: String) {
    val headers = HttpHeaders().apply {
      accept = listOf(MediaType.APPLICATION_JSON)
    }

    val url = "$testServiceUrl/test/$testId"
    logger.info("Sending test run request to: $url")

    try {
      val entity = HttpEntity<Void>(headers)

      restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        Void::class.java
      )
      logger.info("Test run request sent successfully.")
    } catch (e: RestClientException) {
      logger.error("Error sending test run request: ${e.message}")
    }
  }

  /* Test service handles breaking down each individual test for the snippet. */
  fun runAllTests(snippetId: String) {
    val headers = HttpHeaders().apply {
      accept = listOf(MediaType.APPLICATION_JSON)
    }

    val url = "$testServiceUrl/test/$snippetId/all"
    logger.info("Sending run all tests request to: $url")

    try {
      val entity = HttpEntity<Void>(headers)
      restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        Void::class.java
      )
      logger.info("Run all tests request sent successfully.")
    } catch (e: RestClientException) {
      logger.error("Error sending request to run all tests: ${e.message}")
    }
  }
}
