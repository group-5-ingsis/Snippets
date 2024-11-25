package com.ingsis.snippets.snippet

import jakarta.persistence.*
import java.util.UUID

@Entity
data class SnippetConformance(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String = UUID.randomUUID().toString(),

  var snippetId: String,

  var userId: String,

  var complianceStatus: String
) {

  constructor() : this(
    snippetId = "",
    userId = "",
    complianceStatus = "pending"
  )
}
