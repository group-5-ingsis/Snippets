package com.ingsis.snippets.test

data class TestDto(
  val id: String?,
  val name: String?,
  val snippetAuthor: String? = "",
  val input: List<String>?,
  val output: List<String>?
) {
  constructor(testDto: TestDto, snippetAuthor: String) : this(
    testDto.id,
    testDto.name,
    snippetAuthor,
    testDto.input,
    testDto.output)
}
