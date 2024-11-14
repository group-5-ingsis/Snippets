package com.ingsis.snippets.async.producer.test

data class TestResponse(
  val requestId: String,
  val testId: String,
  val passed: Boolean,
)
