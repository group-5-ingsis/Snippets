package com.ingsis.snippets.comment

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Comment(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private var id: String? = null,

  var content: String? = null,

  var date: LocalDateTime = LocalDateTime.now(),

  var snippetId: String? = null,

  var user: String? = null
)
