package com.ingsis.snippets.tests.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.ingsis.snippets.snippet.Snippet
import com.ingsis.snippets.snippet.SnippetRepository
import com.ingsis.snippets.snippet.SnippetWithContent
import com.ingsis.snippets.user.PermissionService
import com.ingsis.snippets.user.UserDto
import org.junit.jupiter.api.AfterAll
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Allows us to use @BeforeAll and maintain state across tests
class UserControllerE2ETests @Autowired constructor(
  private val client: WebTestClient,
  private val snippetRepository: SnippetRepository,
  private val permissionService: PermissionService
) {

  private lateinit var accessToken: String
  private lateinit var sharedSnippetId: String

  @BeforeAll
  fun setup() {
    accessToken = fetchAccessToken()

    val snippet = Snippet(
      author = "auth0|6738e1579d3c4beaae5d1487",
      name = "Shared Snippet",
      language = "Kotlin",
      extension = ".kt",
      compliance = "100%"
    )

    snippetRepository.save(snippet)
    val savedSnippet = snippetRepository.findByName("Shared Snippet")
    permissionService.updatePermissions("auth0|6738e1579d3c4beaae5d1487", savedSnippet.id, "write")
    sharedSnippetId = savedSnippet.id
  }

  @AfterAll
  fun tearDown() {
    snippetRepository.deleteAll()
    for (snippetId in permissionService.getMySnippetsIds("auth0|6738e1579d3c4beaae5d1487")) {
      permissionService.deleteSnippet(snippetId)
    }
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
  fun `should retrieve users`() {
    val users = client.get()
      .uri("/users")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(UserDto::class.java)
      .returnResult()
      .responseBody!!

    assert(users.isNotEmpty()) // Validate the response body
  }

  @Test
  fun `should share snippet with another user`() {
    val userToShareWith = "auth0|example-user-id"
    client.post()
      .uri("/share/$sharedSnippetId/$userToShareWith")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBody(SnippetWithContent::class.java)
      .consumeWith { response ->
        val sharedSnippet = response.responseBody!!
        assert(sharedSnippet.id == sharedSnippetId)
      }
  }
}
