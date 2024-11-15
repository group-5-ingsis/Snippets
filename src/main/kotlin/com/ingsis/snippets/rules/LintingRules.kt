package com.ingsis.snippets.rules

import com.fasterxml.jackson.annotation.JsonProperty

data class LintingRules(
  @JsonProperty("identifierNamingConvention") val identifierNamingConvention: String,
  @JsonProperty("printlnExpressionAllowed") val printlnExpressionAllowed: Boolean,
  @JsonProperty("readInputExpressionAllowed") val readInputExpressionAllowed: Boolean
) : Rules
