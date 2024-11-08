package com.ingsis.snippets.async

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ingsis.snippets.async.producer.format.SnippetFormatRequest

object JsonUtil {
  private val objectMapper: ObjectMapper = jacksonObjectMapper()

  fun serializeToJson(snippetToFormat: SnippetFormatRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToFormat)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }
}
