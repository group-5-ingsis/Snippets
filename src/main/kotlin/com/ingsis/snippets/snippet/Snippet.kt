package com.ingsis.snippets.snippet

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Snippet(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String? = null,

  var title: String? = null,

  var content: String? = null,

  var language: String? = null,

  var creationDate: LocalDateTime? = null,

  var modificationDate: LocalDateTime? = null,

  var userId: Long? = null,

  @ElementCollection
  var comments: List<String>? = mutableListOf(),

  @ElementCollection
  var testCases: List<String>? = mutableListOf(),

  @ElementCollection
  var tags: List<String>? = mutableListOf()
)
