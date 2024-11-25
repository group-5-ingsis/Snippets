package com.ingsis.snippets.snippet

import org.springframework.data.jpa.repository.JpaRepository

interface SnippetComplianceRepository : JpaRepository<SnippetConformance, String> {

  fun findBySnippetIdAndUserId(snippetId: String, userId: String): SnippetConformance?

  fun findAllByUserId(userId: String): List<SnippetConformance>

  fun findAllBySnippetId(snippetId: String): List<SnippetConformance>
}
