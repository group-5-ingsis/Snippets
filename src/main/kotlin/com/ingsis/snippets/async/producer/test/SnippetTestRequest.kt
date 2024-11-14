package com.ingsis.snippets.async.producer.test

/* Contains the test id, and the location inside the AssetService for the snippet. */
data class SnippetTestRequest(
  val testId: String,
  val container: String,
  val key: String
)
