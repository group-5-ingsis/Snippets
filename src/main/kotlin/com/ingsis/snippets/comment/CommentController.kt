package com.ingsis.snippets.comment

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController("/comments")
class CommentController(private val commentService: CommentService) {

  @GetMapping("/{snippetId}")
  fun getCommentsForSnippet(@PathVariable snippetId: String): List<Comment> {
    return commentService.getCommentsBySnippetId(snippetId)
  }
}
