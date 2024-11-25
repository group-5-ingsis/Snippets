package com.ingsis.snippets.snippet

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class SnippetConformance(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String = UUID.randomUUID().toString(),

  var snippetId: String,

  var userId: String,

  var complianceStatus: String,

  var lastChecked: LocalDateTime = LocalDateTime.now()
) {

  constructor() : this(
    snippetId = "",
    userId = "",
    complianceStatus = "unknown"
  )
}
