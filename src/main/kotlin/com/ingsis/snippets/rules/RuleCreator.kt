package com.ingsis.snippets.rules

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ingsis.snippets.format.FormattingRules

object RuleCreator {

  private val objectMapper: ObjectMapper = jacksonObjectMapper()

  fun getDefaultFormattingRules(): String {
    val defaultFormattingRules = FormattingRules(
      spaceBeforeColon = false,
      spaceAfterColon = false,
      spaceAroundAssignment = false,
      newlineAfterPrintln = 0,
      blockIndentation = 0,
      ifBraceSameLine = false,
      singleSpaceSeparation = false
    )

    return objectMapper.writeValueAsString(defaultFormattingRules)
  }
}
