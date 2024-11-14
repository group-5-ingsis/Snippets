package com.ingsis.snippets.test

data class CreateTestDto(
  val snippetId: String,
  val name: String,
  val input: List<String>,
  val output: List<String>
)
