package com.ingsis.snippets.async.test

import com.ingsis.snippets.test.TestDto

data class SnippetCreateTestRequest(
  val snippetId: String,
  val author: String,
  val name: String,
  val input: List<String>?,
  val output: List<String>?,
  val language: String,
  val version: String
) {
  constructor(testDto: TestDto, author: String, language: String, version: String) : this(
    snippetId = testDto.id,
    author = author,
    name = testDto.name,
    input = testDto.input ?: emptyList(),
    output = testDto.output ?: emptyList(),
    language = language,
    version = version
  )
}
