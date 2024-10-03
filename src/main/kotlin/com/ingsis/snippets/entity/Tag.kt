package com.ingsis.snippets.entity

import jakarta.persistence.*

@Entity
class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private var id: Long? = null

  private var name: String? = null

  @ManyToMany(mappedBy = "tags")
  private var snippets: List<Snippet>? = null
}
