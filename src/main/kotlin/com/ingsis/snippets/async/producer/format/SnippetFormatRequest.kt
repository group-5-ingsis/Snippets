package com.ingsis.snippets.async.producer.format

data class SnippetFormatRequest(
  val container: String,
  val key: String,
  val language: String,
  val version: String
)
