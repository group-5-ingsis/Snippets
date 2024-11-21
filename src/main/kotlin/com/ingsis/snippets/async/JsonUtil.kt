package com.ingsis.snippets.async

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ingsis.snippets.async.format.FormatRequest
import com.ingsis.snippets.async.format.FormatResponse
import com.ingsis.snippets.async.lint.LintRequest
import com.ingsis.snippets.async.lint.LintResponse
import com.ingsis.snippets.async.test.TestResponse
import com.ingsis.snippets.rules.*

object JsonUtil {
  private val objectMapper: ObjectMapper = jacksonObjectMapper()

  fun serializeToJson(snippetToFormat: FormatRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToFormat)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun serializeToJson(snippetToFormat: LintRequest): String {
    return try {
      objectMapper.writeValueAsString(snippetToFormat)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }

  fun deserializeRules(rules: String, type: String): Rules {
    return try {
      when (type) {
        FORMATTING_KEY -> objectMapper.readValue(rules, FormattingRules::class.java)
        LINTING_KEY -> objectMapper.readValue(rules, LintingRules::class.java)
        else -> throw IllegalArgumentException("Unknown rule type: $type")
      }
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to deserialize JSON to Rules", e)
    }
  }

  fun deserializeFormatResponse(response: String): FormatResponse {
    return try {
      objectMapper.readValue(response)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to deserialize JSON to FormattingRules", e)
    }
  }

  fun deserializeTestResponse(response: String): TestResponse {
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

  fun serializeRules(rules: Rules): String {
    return try {
      objectMapper.writeValueAsString(rules)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Failed to serialize object to JSON", e)
    }
  }
}
