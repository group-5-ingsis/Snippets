package com.ingsis.snippets.snippet

import java.util.UUID

data class SnippetWithContent(

  var id: String = UUID.randomUUID().toString(),

  var author: String,

  var name: String,

  var language: String,

  var extension: String,

  var content: String
) {
  constructor(snippet: Snippet, content: String) : this(
    id = snippet.id,
    author = snippet.author,
    name = snippet.name,
    language = snippet.language,
    extension = snippet.extension,
    content = content
  )
}

data class SnippetDto(
  var name: String,
  var content: String,
  var language: String,
  var extension: String
)
