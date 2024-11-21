package com.ingsis.snippets.test

import com.ingsis.snippets.async.test.TestedSnippetConsumer
import com.ingsis.snippets.snippet.SnippetController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class TestService(
  private val restTemplate: RestTemplate,
  private val testedSnippetConsumer: TestedSnippetConsumer
) {
  private val logger = LoggerFactory.getLogger(SnippetController::class.java)

  private val testServiceUrl: String = System.getenv("TEST_SERVICE_URL") ?: "http://localhost:8084"

  fun createTest(snippetId: String, testDto: TestDto): TestDto {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
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
      logger.info("Response: " + response.body.toString())

      return response.body ?: throw RuntimeException("Failed to create test. No response body.")
    } catch (e: RestClientException) {
      logger.error("Error sending test creation request: ${e.message}")
      throw RuntimeException("Error sending test creation request: ${e.message}", e)
    }
  }

  fun getAllTestsForSnippet(snippetId: String): List<TestDto> {
    val headers = HttpHeaders().apply {
      contentType = MediaType.APPLICATION_JSON
      accept = listOf(MediaType.ALL)
    }

    val url = "$testServiceUrl/$snippetId"
    logger.info("Sending request to fetch all tests for snippetId $snippetId to URL: $url")
    try {
      val entity = HttpEntity<Void>(headers)

      val response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        Array<Test>::class.java
      )

      val tests = response.body?.toList() ?: emptyList()
      logger.info("Response: $tests")

      return tests.map { test ->
        TestDto(
          name = test.name,
          input = test.userInputs,
          output = test.userOutputs
        )
      }
    } catch (e: RestClientException) {
      logger.error("Error sending test creation request: ${e.message}")
      throw RuntimeException("Error sending test creation request: ${e.message}", e)
    }
  }

  suspend fun runTest(testId: String): Boolean {
    val headers = HttpHeaders().apply {
      accept = listOf(MediaType.APPLICATION_JSON)
    }

    val url = "$testServiceUrl/test/$testId"
    logger.info("Sending test run request to: $url")

    try {
      val entity = HttpEntity<Void>(headers)
      val deferred = testedSnippetConsumer.registerTestResponse(testId)

      withContext(Dispatchers.IO) {
        restTemplate.exchange(
          url,
          HttpMethod.POST,
          entity,
          Void::class.java
        )
      }
      logger.info("Test run request sent successfully.")
      return deferred.await()
    } catch (e: RestClientException) {
      logger.error("Error sending test run request: ${e.message}")
      throw RuntimeException("Error sending test run request: ${e.message}", e)
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
