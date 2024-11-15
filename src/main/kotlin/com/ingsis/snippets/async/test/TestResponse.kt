package com.ingsis.snippets.async.test

data class TestResponse(
  val requestId: String,
  val testId: String,
  val passed: Boolean
)
