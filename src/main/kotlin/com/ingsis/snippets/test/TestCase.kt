package com.ingsis.snippets.test

import jakarta.persistence.*

@Entity
data class TestCase(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String? = null,

  var input: String? = null,

  var expectedOutput: String? = null,

  var snippetId: String? = null,

  var userId: String? = null
)
