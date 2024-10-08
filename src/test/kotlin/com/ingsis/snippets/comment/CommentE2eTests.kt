package com.ingsis.snippets.comment

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CommentE2eTests @Autowired constructor(
  val client: WebTestClient
) {

  @Test
  fun `should return comments for a given snippet`() {
    val snippetId = "snippet1"

    client.get().uri("/comments/snippet/$snippetId").exchange()
      .expectStatus().isOk
      .expectBodyList(Comment::class.java)
      .consumeWith { response ->
        val comments: List<Comment> = response.responseBody ?: emptyList()
        assertEquals(2, comments.size)
        assertEquals("comment1", comments[0].id)
        assertEquals("comment2", comments[1].id)
      }
  }

  @Test
  fun `should return empty list when no comments found for a given snippet`() {
    val snippetId = "snippet2"

    client.get().uri("/comments/snippet/$snippetId").exchange()
      .expectStatus().isOk
      .expectBodyList(Comment::class.java)
      .consumeWith {
        val comments = it.responseBody!!
        assertTrue(comments.isEmpty())
      }
  }
}
