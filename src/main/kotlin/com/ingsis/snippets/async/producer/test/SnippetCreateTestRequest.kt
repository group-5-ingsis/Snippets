package com.ingsis.snippets.async.producer.test

import com.ingsis.snippets.test.CreateTestDto

data class SnippetCreateTestRequest(
  val snippetId: String,
  val author: String,
  val name: String,
  val input: List<String>,
  val output: List<String>,
  val language: String,
  val version: String
) {
  constructor(createTestDto: CreateTestDto, author: String, language: String, version: String) : this(
    snippetId = createTestDto.snippetId,
    author = author,
    name = createTestDto.name,
    input = createTestDto.input,
    output = createTestDto.output,
    language = language,
    version = version
  )
}
