package com.ingsis.snippets.snippet

import com.ingsis.snippets.TestUtils
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class SnippetE2eTests @Autowired constructor(
  val client: WebTestClient
) {

  lateinit var testUtils: TestUtils

  @BeforeEach
  fun setup() {
    testUtils = TestUtils(client)
  }

  @Test
  fun `can create a snippet`() {
    val request = SnippetDto("Sample Snippet", "Sample Content", "Kotlin")

    val createResponse = testUtils.createSnippet(request)
      .expectStatus().isCreated

    val createdSnippet = createResponse.expectBody(Snippet::class.java)
      .returnResult()
      .responseBody!!

    val response = testUtils.getSnippet(createdSnippet.id)
    response.expectBody(SnippetDto::class.java)
      .returnResult()
      .responseBody!!
      .content shouldBeEqualTo "Sample Content"
  }

  @Test
  fun `can get a snippet by id`() {
    val snippet = SnippetDto("Test Snippet", "Test Content", "Kotlin")
    val createResponse = testUtils.createSnippet(snippet).expectStatus().isCreated

    val createdSnippet = createResponse.expectBody(Snippet::class.java)
      .returnResult()
      .responseBody!!

    val response = testUtils.getSnippet(createdSnippet.id)
    response.expectBody(SnippetDto::class.java)
      .returnResult()
      .responseBody!!
      .content shouldBeEqualTo "Test Content"
  }

  @Test
  fun `can update a snippet`() {
    val originalSnippet = SnippetDto("Update Snippet", "Initial Content", "Kotlin")
    val createResponse = testUtils.createSnippet(originalSnippet).expectStatus().isCreated

    val createdSnippet = createResponse.expectBody(Snippet::class.java)
      .returnResult()
      .responseBody!!

    val updatedSnippet = SnippetDto(createdSnippet.title, "Updated Content", "Kotlin")
    testUtils.updateSnippet(createdSnippet.id, updatedSnippet)
      .expectStatus().isOk

    val response = testUtils.getSnippet(createdSnippet.id)
    response.expectBody(SnippetDto::class.java)
      .returnResult()
      .responseBody!!
      .content shouldBeEqualTo "Updated Content"
  }

  @Test
  fun `can delete a snippet`() {
    val snippet = SnippetDto("Delete Snippet", "Content to be deleted", "Kotlin")
    val createResponse = testUtils.createSnippet(snippet).expectStatus().isCreated

    val createdSnippet = createResponse.expectBody(Snippet::class.java)
      .returnResult()
      .responseBody!!

    testUtils.deleteSnippet(createdSnippet.id)
      .expectStatus().isOk

    val response = testUtils.getDeletedSnippet(createdSnippet.id)
    response.expectStatus().isNotFound
  }
}
