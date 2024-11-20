package com.ingsis.snippets.snippet

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepository : JpaRepository<Snippet, String> {
  fun findByName(name: String): List<Snippet>
}
