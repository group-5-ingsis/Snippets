package com.ingsis.snippets.rules

object RuleManager {

  fun getDefaultFormattingRules(): FormattingRules {
    val defaultFormattingRules = FormattingRules(
      spaceBeforeColon = false,
      spaceAfterColon = false,
      spaceAroundAssignment = false,
      newlineAfterPrintln = 0,
      blockIndentation = 0,
      ifBraceSameLine = false,
      singleSpaceSeparation = false
    )
    return defaultFormattingRules
  }

  fun getDefaultLintingRules(): LintingRules {
    val defaultLintingRules = LintingRules(
      identifierNamingConvention = "snake-case",
      printlnExpressionAllowed = false,
      readInputExpressionAllowed = false
    )
    return defaultLintingRules
  }

  fun convertToRuleList(formattingRules: FormattingRules): List<RuleDto> {
    return listOf(
      RuleDto(id = "1", name = "spaceBeforeColon", isActive = formattingRules.spaceBeforeColon, value = null),
      RuleDto(id = "2", name = "spaceAfterColon", isActive = formattingRules.spaceAfterColon, value = null),
      RuleDto(id = "3", name = "spaceAroundAssignment", isActive = formattingRules.spaceAroundAssignment, value = null),
      RuleDto(id = "4", name = "newlineAfterPrintln", isActive = true, value = formattingRules.newlineAfterPrintln),
      RuleDto(id = "5", name = "blockIndentation", isActive = true, value = formattingRules.blockIndentation),
      RuleDto(id = "6", name = "if-brace-same-line", isActive = formattingRules.ifBraceSameLine, value = null),
      RuleDto(id = "7", name = "mandatory-single-space-separation", isActive = formattingRules.singleSpaceSeparation, value = null)
    )
  }

  fun convertToRuleList(lintingRules: LintingRules): List<RuleDto> {
    return listOf(
      RuleDto(id = "1", name = "identifierNamingConvention", isActive = true, value = lintingRules.identifierNamingConvention),
      RuleDto(id = "2", name = "printlnExpressionAllowed", isActive = lintingRules.printlnExpressionAllowed, value = null),
      RuleDto(id = "3", name = "readInputExpressionAllowed", isActive = lintingRules.readInputExpressionAllowed, value = null)
    )
  }
}
