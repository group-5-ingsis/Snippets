package com.ingsis.snippets.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, String> {
  fun findBySnippetId(snippetId: String): List<Comment>
}
