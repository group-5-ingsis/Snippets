package com.ingsis.snippets.test

import jakarta.persistence.*

@Entity
class TestCase {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private var id: String? = null

  private var input: String? = null
  private var expectedOutput: String? = null

  private var snippetId: String? = null

  private var userId: String? = null
}
