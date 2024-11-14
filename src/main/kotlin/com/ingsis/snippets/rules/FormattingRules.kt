package com.ingsis.snippets.rules

data class FormattingRules(
  val spaceBeforeColon: Boolean,
  val spaceAfterColon: Boolean,
  val spaceAroundAssignment: Boolean,
  val newlineAfterPrintln: Int,
  val blockIndentation: Int,
  val ifBraceSameLine: Boolean,
  val singleSpaceSeparation: Boolean
)
