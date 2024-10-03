package com.ingsis.snippets.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Comment(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private var id: Long? = null,

  private var content: String? = null,

  private var date: LocalDateTime = LocalDateTime.now(),

  @ManyToOne
  @JoinColumn(name = "snippet_id")
  private var snippet: Snippet? = null,

  @ManyToOne
  @JoinColumn(name = "user_id")
  private var user: User? = null
)
