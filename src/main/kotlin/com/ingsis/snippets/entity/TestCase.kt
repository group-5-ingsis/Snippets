package com.ingsis.snippets.entity

import jakarta.persistence.*

@Entity
class TestCase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private var id: Long? = null

  private var input: String? = null
  private var expectedOutput: String? = null

  @ManyToOne
  @JoinColumn(name = "snippet_id")
  private var snippet: Snippet? = null

  @ManyToOne
  @JoinColumn(name = "user_id")
  private var user: User? = null
}
