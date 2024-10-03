package com.ingsis.snippets.tag

import jakarta.persistence.*

@Entity
data class Tag(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String? = null,

  var name: String? = null,

  var snippetId: String? = null

)
