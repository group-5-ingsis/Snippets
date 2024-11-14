package com.ingsis.snippets.async.producer.lint

data class LintRequest(
  val requestId: String,
  val author: String,
  val snippet: String
)
