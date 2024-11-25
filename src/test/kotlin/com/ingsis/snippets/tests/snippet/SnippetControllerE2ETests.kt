package com.ingsis.snippets.tests.snippet

import com.fasterxml.jackson.databind.ObjectMapper
import com.ingsis.snippets.snippet.Snippet
import com.ingsis.snippets.snippet.SnippetDto
import com.ingsis.snippets.snippet.SnippetRepository
import com.ingsis.snippets.snippet.SnippetWithContent
import com.ingsis.snippets.user.PermissionService
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnippetControllerE2ETests @Autowired constructor(
  private val client: WebTestClient,
  private val snippetRepository: SnippetRepository,
  private val permissionService: PermissionService
) {

  private lateinit var accessToken: String
  private lateinit var firstSnippetId: String

  @BeforeAll
  fun setup() {
    accessToken = fetchAccessToken()

    val snippet1 = Snippet(
      author = "whahw",
      name = "Snippet One",
      language = "PrintScript 1.0",
      extension = ".ps",
      compliance = "100%"
    )
    val snippet2 = Snippet(
      author = "whahw",
      name = "Snippet Two",
      language = "PrintScript 1.0",
      extension = ".ps",
      compliance = "95%"
    )

    snippetRepository.save(snippet1)
    val savedSnippet1 = snippetRepository.findByName("Snippet One")
    permissionService.updatePermissions("write", "add", "auth0|6738e1579d3c4beaae5d1487", savedSnippet1.id)
    firstSnippetId = savedSnippet1.id

    snippetRepository.save(snippet2)
    val savedSnippet2 = snippetRepository.findByName("Snippet Two")
    permissionService.updatePermissions("write", "add", "auth0|6738e1579d3c4beaae5d1487", savedSnippet2.id)
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
  fun dummy() {
    assert(true)
  }

  @Test
  fun `should create a snippet`() {
    val snippetDto = SnippetDto(
      name = "Snippet Three",
      content = "print('Hello, World!')",
      language = "PrintScript 1.0",
      extension = ".ps"
    )
    client.post()
      .uri("/")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(snippetDto)
      .exchange()
      .expectStatus().isOk
      .expectBody(Snippet::class.java)
      .consumeWith { response ->
        assert(response.responseBody?.name == "Snippet Three")
      }
  }

  @Test
  fun `should fetch snippet by id`() {
    val snippet = snippetRepository.findAll().first()
    client.get()
      .uri("/id/${snippet.id}")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBody(SnippetWithContent::class.java)
      .consumeWith { response ->
        val fetchedSnippet = response.responseBody!!
        assert(fetchedSnippet.id == snippet.id)
        assert(fetchedSnippet.name == snippet.name)
      }
  }

  @Test
  fun `should fetch snippets by name`() {
    val response = client.get()
      .uri("/name/Snippet One")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(Snippet::class.java)
      .returnResult()

    val snippets = response.responseBody!!
    assert(snippets.size == 1)
    assert(snippets[0].name == "Snippet One")
  }

  @Test
  fun `should fetch all snippets for a user`() {
    val response = client.get()
      .uri("/")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(Snippet::class.java)
      .returnResult()

    val snippets = response.responseBody!!
    assert(snippets.size == 2)
  }

  @Test
  fun `should update a snippet`() {
    val snippet = snippetRepository.findAll().first()
    client.put()
      .uri("/${snippet.id}")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue("Updated Content")
      .exchange()
      .expectStatus().isOk
      .expectBody(SnippetWithContent::class.java)
      .consumeWith { response ->
        val updatedSnippet = response.responseBody!!
        assert(updatedSnippet.content == "Updated Content")
      }
  }

  @Test
  fun `should delete a snippet`() {
    client.delete()
      .uri("/$firstSnippetId")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk

    assert(snippetRepository.findById(firstSnippetId).isEmpty)
  }
}
