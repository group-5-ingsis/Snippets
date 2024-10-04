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
class SnippetControllerE2ETests @Autowired constructor(
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
    testUtils.createSnippet(request)
      .expectStatus().isCreated

    val response = testUtils.getSnippet("Sample Snippet")
    response.expectBody(SnippetDto::class.java)
      .returnResult()
      .responseBody!!
      .content shouldBeEqualTo "Sample Content"
  }

  @Test
  fun `can get a snippet by id`() {
    val snippet = SnippetDto("Test Snippet", "Test Content", "Kotlin")
    testUtils.createSnippet(snippet).expectStatus().isCreated

    val response = testUtils.getSnippet("Test Snippet")
    response.expectBody(SnippetDto::class.java)
      .returnResult()
      .responseBody!!
      .content shouldBeEqualTo "Test Content"
  }

  @Test
  fun `can update a snippet`() {
    val originalSnippet = SnippetDto("Update Snippet", "Initial Content", "Kotlin")
    testUtils.createSnippet(originalSnippet).expectStatus().isCreated

    val updatedSnippet = SnippetDto("Update Snippet", "Updated Content", "Kotlin")
    testUtils.updateSnippet("Update Snippet", updatedSnippet)
      .expectStatus().isOk

    val response = testUtils.getSnippet("Update Snippet")
    response.expectBody(SnippetDto::class.java)
      .returnResult()
      .responseBody!!
      .content shouldBeEqualTo "Updated Content"
  }

  @Test
  fun `can delete a snippet`() {
    val snippet = SnippetDto("Delete Snippet", "Content to be deleted", "Kotlin")
    testUtils.createSnippet(snippet).expectStatus().isCreated

    testUtils.deleteSnippet("Delete Snippet")
      .expectStatus().isOk

    val response = testUtils.getSnippet("Delete Snippet")
    response.expectStatus().isNotFound
  }
}
