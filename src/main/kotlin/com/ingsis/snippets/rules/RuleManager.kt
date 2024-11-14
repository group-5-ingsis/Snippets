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
      identifierNamingConvention = "snake case",
      printlnExpressionAllowed = false,
      readInputExpressionAllowed = false
    )
    return defaultLintingRules
  }

  fun convertToRuleList(formattingRules: FormattingRules): List<Rule> {
    return listOf(
      Rule(id = "1", name = "spaceBeforeColon", isActive = formattingRules.spaceBeforeColon, value = null),
      Rule(id = "2", name = "spaceAfterColon", isActive = formattingRules.spaceAfterColon, value = null),
      Rule(id = "3", name = "spaceAroundAssignment", isActive = formattingRules.spaceAroundAssignment, value = null),
      Rule(id = "4", name = "newlineAfterPrintln", isActive = true, value = formattingRules.newlineAfterPrintln),
      Rule(id = "5", name = "blockIndentation", isActive = true, value = formattingRules.blockIndentation),
      Rule(id = "6", name = "ifBraceSameLine", isActive = formattingRules.ifBraceSameLine, value = null),
      Rule(id = "7", name = "singleSpaceSeparation", isActive = formattingRules.singleSpaceSeparation, value = null)
    )
  }

  fun convertToRuleList(lintingRules: LintingRules): List<Rule> {
    return listOf(
      Rule(id = "1", name = "identifierNamingConvention", isActive = true, value = lintingRules.identifierNamingConvention),
      Rule(id = "2", name = "printlnExpressionAllowed", isActive = lintingRules.printlnExpressionAllowed, value = null),
      Rule(id = "3", name = "readInputExpressionAllowed", isActive = lintingRules.readInputExpressionAllowed, value = null)
    )
  }
}
