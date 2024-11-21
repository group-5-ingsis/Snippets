package com.ingsis.snippets.async.lint

data class LintRequest(
  val requestId: String,
  val author: String,
  val snippet: String
)
