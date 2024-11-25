package com.ingsis.snippets.snippet

import jakarta.persistence.*
import java.util.UUID

@Entity
data class Snippet(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String = UUID.randomUUID().toString(),

  var author: String,

  var name: String,

  var language: String,

  var extension: String

) {
  constructor() : this(
    author = "",
    name = "",
    language = "",
    extension = ""
  )

  constructor(snippetDto: SnippetDto) : this(
    author = "",
    name = snippetDto.name,
    language = snippetDto.language,
    extension = snippetDto.extension
  )
}
