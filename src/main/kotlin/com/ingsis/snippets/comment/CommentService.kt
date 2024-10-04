package com.ingsis.snippets.comment

import org.springframework.stereotype.Service

@Service
class CommentService(private val commentRepository: CommentRepository) {

  fun getCommentsBySnippetId(snippetId: String): List<Comment> {
    return commentRepository.findBySnippetId(snippetId)
  }
}
