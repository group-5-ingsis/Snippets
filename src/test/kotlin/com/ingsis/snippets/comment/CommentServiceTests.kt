package com.ingsis.snippets.comment

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CommentServiceTests {

  private val commentRepository = mock(CommentRepository::class.java)
  private val commentService = CommentService(commentRepository)

  @Test
  fun `should return comments for a given snippet`() {
    val snippetId = "snippet1"
    val expectedComments = listOf(
      Comment(id = "comment1", content = "Comment 1", snippetId = snippetId, user = "user1"),
      Comment(id = "comment2", content = "Comment 2", snippetId = snippetId, user = "user2")
    )

    whenever(commentRepository.findBySnippetId(snippetId)).thenReturn(expectedComments)
    val comments = commentService.getCommentsBySnippetId(snippetId)

    assertEquals(2, comments.size)
    assertEquals("comment1", comments[0].id)
    assertEquals("comment2", comments[1].id)
  }

  @Test
  fun `should return empty list when no comments found for a given snippet`() {
    val snippetId = "snippet2"
    val expectedComments = emptyList<Comment>()

    whenever(commentRepository.findBySnippetId(snippetId)).thenReturn(expectedComments)
    val comments = commentService.getCommentsBySnippetId(snippetId)

    assertTrue(comments.isEmpty())
  }
}
