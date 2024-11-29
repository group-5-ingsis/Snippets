package com.ingsis.snippets.tests.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.ingsis.snippets.snippet.Snippet
import com.ingsis.snippets.snippet.SnippetRepository
import com.ingsis.snippets.test.TestDto
import com.ingsis.snippets.user.PermissionService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = ["spring.profiles.active=test"]
)
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestControllerE2ETests @Autowired constructor(
  private val client: WebTestClient,
  private val snippetRepository: SnippetRepository,
  private val permissionService: PermissionService
) {

  private lateinit var accessToken: String
  private lateinit var snippetId: String
  private val userId = "auth0|6738e1579d3c4beaae5d1487" // Adjust as necessary

  @BeforeAll
  fun setup() {
    accessToken = fetchAccessToken()

    // Create a snippet to associate tests with
    val snippet = Snippet(
      author = userId,
      name = "Snippet for Tests",
      language = "Kotlin",
      extension = ".kt",
      compliance = "100%"
    )

    snippetRepository.save(snippet)
    val savedSnippet = snippetRepository.findByName("Snippet for Tests")
    snippetId = savedSnippet.id

    // Update permissions for the snippet
    permissionService.updatePermissions("write", "add", userId, snippetId)
  }

  @AfterAll
  fun tearDown() {
    snippetRepository.deleteAll()
    for (id in permissionService.getMySnippetsIds(userId)) {
      permissionService.deleteSnippet(id)
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
  fun `should create a test for a snippet`() {
    val testDto = TestDto(
      id = null,
      name = "Test 1",
      input = listOf("input1"),
      output = listOf("output1")
    )

    client.post()
      .uri("/test/$snippetId")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(testDto)
      .exchange()
      .expectStatus().isOk
      .expectBody(TestDto::class.java)
      .consumeWith { response ->
        val createdTest = response.responseBody
        Assertions.assertNotNull(createdTest, "Created test should not be null")
        Assertions.assertEquals("Test 1", createdTest!!.name)
        Assertions.assertEquals(listOf("input1"), createdTest.input)
        Assertions.assertEquals(listOf("output1"), createdTest.output)
      }
  }

  @Test
  fun `should fetch all tests for a snippet`() {
    val testDto = TestDto(
      id = null,
      name = "Test 1",
      input = listOf("input1"),
      output = listOf("output1")
    )

    client.post()
      .uri("/test/$snippetId")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(testDto)
      .exchange()

    val tests = client.get()
      .uri("/test/$snippetId")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(TestDto::class.java)
      .returnResult()
      .responseBody

    Assertions.assertNotNull(tests, "Response body should not be null")
    Assertions.assertTrue(tests!!.isNotEmpty(), "Tests should not be empty")
  }

  @Test
  fun `should remove a test`() {
    val testDto = TestDto(
      id = null,
      name = "Test 1",
      input = listOf("input1"),
      output = listOf("output1")
    )

    val response = client.post()
      .uri("/test/$snippetId")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(testDto)
      .exchange()
      .expectStatus().isOk
      .expectBodyList(TestDto::class.java)
      .returnResult()
      .responseBody

    val testIdToDelete: String? = response?.first()?.id

    // Delete the test
    client.delete()
      .uri("/test/$testIdToDelete")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk

    // Verify deletion
    val tests = client.get()
      .uri("/test/$snippetId")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(TestDto::class.java)
      .returnResult()
      .responseBody

    Assertions.assertNotNull(tests, "Response body should not be null")
    Assertions.assertFalse(tests!!.any { it.id == testIdToDelete }, "Test should be deleted")
  }

  @Test
  fun `should run all tests for a snippet`() {
    client.post()
      .uri("/test/run/$snippetId/all")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
  }
}
