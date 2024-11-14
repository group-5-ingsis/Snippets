package com.ingsis.snippets.async

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ingsis.snippets.async.producer.format.FormatRequest
import com.ingsis.snippets.async.producer.format.FormatResponse
import com.ingsis.snippets.async.producer.lint.LintResponse
import com.ingsis.snippets.async.producer.lint.SnippetLintRequest
import com.ingsis.snippets.async.producer.test.SnippetCreateTestRequest
import com.ingsis.snippets.async.producer.test.SnippetTestRequest
import com.ingsis.snippets.rules.FormattingRules
import com.ingsis.snippets.rules.LintingRules

object JsonUtil {
  private val objectMapper: ObjectMapper = jacksonObjectMapper()

  fun serializeToJson(snippetToFormat: FormatRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToFormat)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun serializeToJson(snippetToFormat: SnippetLintRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToFormat)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun deserializeFormattingRules(rules: String): FormattingRules {
    return try {
      objectMapper.readValue(rules)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to deserialize JSON to FormattingRules", e)
    }
  }

  fun deserializeLintingRules(rules: String): LintingRules {
    return try {
      objectMapper.readValue(rules)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to deserialize JSON to FormattingRules", e)
    }
  }

  fun deserializeFormatResponse(response: String): FormatResponse {
    return try {
      objectMapper.readValue(response)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to deserialize JSON to FormattingRules", e)
    }
  }

  fun deserializeLintResponse(response: String): LintResponse {
    return try {
      objectMapper.readValue(response)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to deserialize JSON to FormattingRules", e)
    }
  }

  fun serializeFormattingRules(rules: FormattingRules): String {
    return try {
      objectMapper.writeValueAsString(rules)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun serializeLintingRules(rules: LintingRules): String {
    return try {
      objectMapper.writeValueAsString(rules)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun serializeTestToJson(snippetToTest: SnippetTestRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToTest)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun serializeCreateTestToJson(snippetToTest: SnippetCreateTestRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToTest)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }
}
