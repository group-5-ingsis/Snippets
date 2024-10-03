package com.ingsis.snippets.tag

import jakarta.persistence.*

@Entity
class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private var id: String? = null

  private var name: String? = null

  @ElementCollection
  private var snippets: List<String>? = null
}
