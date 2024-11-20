package com.ingsis.snippets.snippet

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = ["spring.profiles.active=test"])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class SnippetControllerE2ETests @Autowired constructor(
  private val client: WebTestClient,
  private val snippetRepository: SnippetRepository
) {

  private lateinit var accessToken: String

  @BeforeEach
  fun setup() {
    accessToken = fetchAccessToken()
    println("Access Token: $accessToken")
    val snippet1 = Snippet(
      author = "user1",
      name = "Snippet One",
      language = "Kotlin",
      extension = ".kt",
      compliance = "100%"
    )
    val snippet2 = Snippet(
      author = "user1",
      name = "Snippet Two",
      language = "Java",
      extension = ".java",
      compliance = "95%"
    )
    snippetRepository.save(snippet1)
    snippetRepository.save(snippet2)
  }

  @AfterEach
  fun tearDown() {
    snippetRepository.deleteAll()
  }

  private fun fetchAccessToken(): String {
    val process = ProcessBuilder()
      .command(
        "curl",
        "--location",
        "https://dev-baomkzt76ougszgg.us.auth0.com/oauth/token",
        "--data-urlencode", "grant_type=password",
        "--data-urlencode", "username=test@test.com",
        "--data-urlencode", "password=Hola123!",
        "--data-urlencode", "scope=read:snippets write:snippets",
        "--data-urlencode", "audience=https://snippets",
        "--data-urlencode", "client_id=eloEUq9yJ9yFVYegEf72MDGTa6HF4KI7",
        "--data-urlencode", "client_secret=BjT6pO9gPGTVc4XVIPGlk7XrA6pLgAEcQmAIbKce1aP6VMqcJkpjEvMINjhAvy8v"
      )
      .redirectErrorStream(true)
      .start()

    val rawResponse = process.inputStream.bufferedReader().readText()
    println("Raw Token Response: $rawResponse")

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
      language = "Python",
      extension = ".py"
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
    val snippet = snippetRepository.findAll().first()
    client.delete()
      .uri("/${snippet.id}")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk

    client.get()
      .uri("/id/${snippet.id}")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isNotFound
  }
}
