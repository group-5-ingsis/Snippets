package com.ingsis.snippets.snippet

import com.ingsis.snippets.snippet.model.Snippet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SnippetRepository : JpaRepository<Snippet, String>
