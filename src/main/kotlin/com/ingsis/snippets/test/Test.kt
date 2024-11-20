package com.ingsis.snippets.test

data class Test (
  val id: String,
  val snippetId: String,
  val name: String,
  val snippetAuthor: String,
  val userInputs: List<String>,
  val userOutputs: List<String>,
  val testPassed: TestStatus
)

enum class TestStatus {
  PENDING,
  PASSED,
  FAILED
}
