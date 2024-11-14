package com.ingsis.snippets.async.producer.lint

data class SnippetLintRequest(
  val requestId: String,
  val author: String,
  val snippet: String
)
