package com.ingsis.snippets.snippet

import jakarta.persistence.*
import java.util.UUID

@Entity
data class Snippet(

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  var id: String = UUID.randomUUID().toString(),

  var author: String,

  var description: String,

  var name: String,

  var version: String,

  var language: String,

  var compliant: String
) {
  constructor() : this(
    id = "",
    name = "",
    description = "",
    version = "",
    language = "",
    compliant = "",
    author = ""
  )

  constructor(snippetDto: SnippetDto) : this(
    id = "",
    author = snippetDto.author,
    description = snippetDto.description,
    name = snippetDto.name,
    version = snippetDto.version,
    language = snippetDto.language,
    compliant = "unknown"
  )
}
