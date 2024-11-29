package com.ingsis.snippets.tests.language

import com.fasterxml.jackson.databind.ObjectMapper
import com.ingsis.snippets.language.Language
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LanguageControllerE2ETests @Autowired constructor(
  private val client: WebTestClient
) {

  private lateinit var accessToken: String

  @BeforeAll
  fun setup() {
    accessToken = fetchAccessToken()
  }

  private fun fetchAccessToken(): String {
    val process = ProcessBuilder()
      .command(
        "curl",
        "--location",
        System.getenv("AUTH_SERVER_URI"),
        "--data-urlencode", "grant_type=password",
        "--data-urlencode", "username=${System.getenv("AUTH_USERNAME")}",
        "--data-urlencode", "password=${System.getenv("AUTH_PASSWORD")}",
        "--data-urlencode", "scope=${System.getenv("AUTH_SCOPE")}",
        "--data-urlencode", "audience=${System.getenv("AUTH0_AUDIENCE")}",
        "--data-urlencode", "client_id=${System.getenv("AUTH_CLIENT_ID")}",
        "--data-urlencode", "client_secret=${System.getenv("AUTH_CLIENT_SECRET")}"
      )
      .redirectErrorStream(true)
      .start()

    val rawResponse = process.inputStream.bufferedReader().readText()

    val jsonStartIndex = rawResponse.indexOf('{')
    val jsonEndIndex = rawResponse.lastIndexOf('}')
    if (jsonStartIndex == -1 || jsonEndIndex == -1) {
      throw IllegalArgumentException("No valid JSON object found in the response")
    }
    val jsonResponse = rawResponse.substring(jsonStartIndex, jsonEndIndex + 1)

    val tokenResponse = ObjectMapper().readTree(jsonResponse)
    return tokenResponse["access_token"].asText()
  }

  @Test
  fun `should fetch all file types`() {
    val fileTypes = client.get()
      .uri("/language/types")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(Language::class.java)
      .returnResult()
      .responseBody!!

    assertTrue(fileTypes.isNotEmpty(), "File types should not be empty")
  }
}
