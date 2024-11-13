package com.ingsis.snippets.async.producer.test

data class SnippetCreateTestRequest(
  val snippetId: String,
  val name: String,
  val input: List<String>,
  val output: List<String>,
  val language: String,
  val version: String,
) {
  constructor(createTestDto: CreateTestDto, language: String, version: String) : this(
    snippetId = createTestDto.snippetId,
    name = createTestDto.name,
    input = createTestDto.input,
    output = createTestDto.output,
    language = language,
    version = version
  )
}
