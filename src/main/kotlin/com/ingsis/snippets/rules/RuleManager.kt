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
      Rule(id = "6", name = "if-brace-same-line", isActive = formattingRules.ifBraceSameLine, value = null),
      Rule(id = "7", name = "mandatory-single-space-separation", isActive = formattingRules.singleSpaceSeparation, value = null)
    )
  }

  fun convertToRuleList(lintingRules: LintingRules): List<Rule> {
    return listOf(
      Rule(id = "1", name = "identifierNamingConvention", isActive = true, value = lintingRules.identifierNamingConvention),
      Rule(id = "2", name = "printlnExpressionAllowed", isActive = lintingRules.printlnExpressionAllowed, value = null),
      Rule(id = "3", name = "readInputExpressionAllowed", isActive = lintingRules.readInputExpressionAllowed, value = null)
    )
  }

  fun convertToFormattingRules(rules: List<Rule>): FormattingRules {
    return FormattingRules(
      spaceBeforeColon = rules.find { it.name == "spaceBeforeColon" }?.isActive == true,
      spaceAfterColon = rules.find { it.name == "spaceAfterColon" }?.isActive == true,
      spaceAroundAssignment = rules.find { it.name == "spaceAroundAssignment" }?.isActive == true,
      newlineAfterPrintln = rules.find { it.name == "newlineAfterPrintln" }?.value as? Int ?: 0,
      blockIndentation = rules.find { it.name == "blockIndentation" }?.value as? Int ?: 0,
      ifBraceSameLine = rules.find { it.name == "if-brace-same-line" }?.isActive == true,
      singleSpaceSeparation = rules.find { it.name == "mandatory-single-space-separation" }?.isActive == true
    )
  }

  fun convertToLintingRules(rules: List<Rule>): LintingRules {
    return LintingRules(
      identifierNamingConvention = rules.find { it.name == "identifierNamingConvention" }?.value as? String ?: "snake case",
      printlnExpressionAllowed = rules.find { it.name == "printlnExpressionAllowed" }?.isActive == true,
      readInputExpressionAllowed = rules.find { it.name == "readInputExpressionAllowed" }?.isActive == true
    )
  }
}
