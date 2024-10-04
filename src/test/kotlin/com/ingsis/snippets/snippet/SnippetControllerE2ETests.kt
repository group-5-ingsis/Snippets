package com.ingsis.snippets.snippet

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@ActiveProfiles(value = ["test"])
@AutoConfigureWebTestClient
class SnippetControllerE2ETests @Autowired constructor(
  val client: WebTestClient,
  val repository: SnippetRepository
) {

  private val base = "/snippets"

  @BeforeEach
  fun setup() {
    repository.saveAll(
      listOf(
        Snippet(UUID.randomUUID().toString(), "Test Snippet 1", "Content for Snippet 1", "Kotlin"),
        Snippet(UUID.randomUUID().toString(), "Test Snippet 2", "Content for Snippet 2", "Java")
      )
    )
  }

  @Test
  fun `can get all snippets`() {
    getAllSnippets()
      .expectBodyList(Snippet::class.java)
      .hasSize(2)
  }

  @Test
  fun `can create a snippet`() {
    val request = SnippetDto("New Snippet", "This is a new snippet", "Kotlin")
    createSnippet(request).expectStatus().isCreated

    val response = getAllSnippets()

    val list = response.expectBodyList(Snippet::class.java)
    list.hasSize(3)
  }

  @Test
  fun `cannot create a snippet with missing title`() {
    val request = SnippetDto("", "This snippet has no title", "Kotlin")
    createSnippet(request).expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)

    val response = getAllSnippets()
    val list = response.expectBodyList(Snippet::class.java)
    list.hasSize(2)
  }

  @Test
  fun `can get a specific snippet by ID`() {
    val snippets = getAllSnippets().expectBodyList(Snippet::class.java).returnResult().responseBody!!
    val snippetId = snippets[0].id

    if (snippetId != null) {
      val response = getSnippet(snippetId)
      response.expectStatus().isOk
      response.expectBody().jsonPath("$.title").isEqualTo("Test Snippet 1")
    }
  }

  @Test
  fun `can update an existing snippet`() {
    val snippets = getAllSnippets().expectBodyList(Snippet::class.java).returnResult().responseBody!!
    val snippetId = snippets[0].id

    if (snippetId != null) {
      val updatedSnippet = SnippetDto("Updated Title", "Updated Content", "Java")
      updateSnippet(snippetId, updatedSnippet).expectStatus().isOk

      val response = getSnippet(snippetId)
      response.expectBody()
        .jsonPath("$.title").isEqualTo("Updated Title")
    }
  }

  @Test
  fun `can delete an existing snippet`() {
    val snippets = getAllSnippets().expectBodyList(Snippet::class.java).returnResult().responseBody!!
    val snippetId = snippets[0].id

    if (snippetId != null) {
      deleteSnippet(snippetId).expectStatus().isOk
      getAllSnippets().expectBodyList(Snippet::class.java).hasSize(1)
    }
  }

  @AfterEach
  fun resetDB() {
    repository.deleteAll()
  }

  private fun createSnippet(request: SnippetDto): WebTestClient.ResponseSpec {
    return client.post().uri(base).bodyValue(request)
      .exchange()
  }

  private fun getSnippet(snippetId: String): WebTestClient.ResponseSpec {
    return client.get().uri("$base/$snippetId")
      .exchange()
      .expectStatus().isOk
  }

  private fun getAllSnippets(): WebTestClient.ResponseSpec {
    return client.get().uri(base)
      .exchange()
      .expectStatus().isOk
  }

  private fun updateSnippet(snippetId: String, request: SnippetDto): WebTestClient.ResponseSpec {
    return client.put().uri("$base/update/$snippetId").bodyValue(request)
      .exchange()
      .expectStatus().isOk
  }

  private fun deleteSnippet(snippetId: String): WebTestClient.ResponseSpec {
    return client.delete().uri("$base/delete/$snippetId")
      .exchange()
      .expectStatus().isOk
  }
}
