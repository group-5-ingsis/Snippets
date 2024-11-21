package com.ingsis.snippets.rules

import com.fasterxml.jackson.annotation.JsonProperty

sealed class Rules

data class RuleDto(
  val id: String,
  val name: String,
  val isActive: Boolean,
  val value: Any? = null
)

data class FormattingRules(
  @JsonProperty("spaceBeforeColon") val spaceBeforeColon: Boolean,
  @JsonProperty("spaceAfterColon") val spaceAfterColon: Boolean,
  @JsonProperty("spaceAroundAssignment") val spaceAroundAssignment: Boolean,
  @JsonProperty("newlineAfterPrintln") val newlineAfterPrintln: Int,
  @JsonProperty("blockIndentation") val blockIndentation: Int,
  @JsonProperty("if-brace-same-line") val ifBraceSameLine: Boolean
) : Rules()

data class LintingRules(
  @JsonProperty("identifierNamingConvention") val identifierNamingConvention: String,
  @JsonProperty("printlnExpressionAllowed") val printlnExpressionAllowed: Boolean,
  @JsonProperty("readInputExpressionAllowed") val readInputExpressionAllowed: Boolean
) : Rules()

const val FORMATTING_KEY = "FormattingRules"
const val LINTING_KEY = "LintingRules"
