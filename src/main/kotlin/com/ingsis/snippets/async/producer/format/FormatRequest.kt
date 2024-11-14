package com.ingsis.snippets.async.producer.format

data class FormatRequest(
  val requestId: String,
  val author: String,
  val snippet: String
)
