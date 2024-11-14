package com.ingsis.snippets.rules

data class LintingRules(
  val identifierNamingConvention: String,
  val printlnExpressionAllowed: Boolean,
  val readInputExpressionAllowed: Boolean
)
