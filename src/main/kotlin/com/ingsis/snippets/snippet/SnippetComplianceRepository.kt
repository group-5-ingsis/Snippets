package com.ingsis.snippets.snippet

import org.springframework.data.jpa.repository.JpaRepository

interface SnippetComplianceRepository : JpaRepository<SnippetCompliance, String> {

  fun findBySnippetIdAndUserId(snippetId: String, userId: String): SnippetCompliance?

  fun findAllByUserId(userId: String): List<SnippetCompliance>

  fun findAllBySnippetId(snippetId: String): List<SnippetCompliance>
}
