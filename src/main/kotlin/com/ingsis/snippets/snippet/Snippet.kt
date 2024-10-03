package com.ingsis.snippets.snippet

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Snippet {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private var id: String? = null

  private var title: String? = null

  private var content: String? = null

  private var language: String? = null

  private var creationDate: LocalDateTime? = null

  private var modificationDate: LocalDateTime? = null

  private var userId: Long? = null

  @ElementCollection
  private var comments: List<String>? = mutableListOf()

  @ElementCollection
  private var testCases: List<String>? = mutableListOf()

  @ElementCollection
  private var tags: List<String>? = mutableListOf()
}
