package com.ingsis.snippets.rules

object RuleManager {

  fun getDefaultFormattingRules(): FormattingRules {
    val defaultFormattingRules = FormattingRules(
      spaceBeforeColon = false,
      spaceAfterColon = false,
      spaceAroundAssignment = false,
      newlineAfterPrintln = 0,
      blockIndentation = 0,
      ifBraceSameLine = false
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
      RuleDto(id = "6", name = "if-brace-same-line", isActive = formattingRules.ifBraceSameLine, value = null)
    )
  }

  fun convertToRuleList(lintingRules: LintingRules): List<RuleDto> {
    return listOf(
      RuleDto(id = "1", name = "identifierNamingConvention", isActive = true, value = lintingRules.identifierNamingConvention),
      RuleDto(id = "2", name = "printlnExpressionAllowed", isActive = lintingRules.printlnExpressionAllowed, value = null),
      RuleDto(id = "3", name = "readInputExpressionAllowed", isActive = lintingRules.readInputExpressionAllowed, value = null)
    )
  }

  fun convertToFormattingRules(ruleDtos: List<RuleDto>): FormattingRules {
    val spaceBeforeColon = ruleDtos.first { it.name == "spaceBeforeColon" }.isActive
    val spaceAfterColon = ruleDtos.first { it.name == "spaceAfterColon" }.isActive
    val spaceAroundAssignment = ruleDtos.first { it.name == "spaceAroundAssignment" }.isActive
    val newlineAfterPrintln = ruleDtos.first { it.name == "newlineAfterPrintln" }.value as? Int ?: 0
    val blockIndentation = ruleDtos.first { it.name == "blockIndentation" }.value as? Int ?: 0
    val ifBraceSameLine = ruleDtos.first { it.name == "if-brace-same-line" }.isActive

    return FormattingRules(
      spaceBeforeColon = spaceBeforeColon,
      spaceAfterColon = spaceAfterColon,
      spaceAroundAssignment = spaceAroundAssignment,
      newlineAfterPrintln = newlineAfterPrintln,
      blockIndentation = blockIndentation,
      ifBraceSameLine = ifBraceSameLine
    )
  }

  fun convertToLintingRules(ruleDtos: List<RuleDto>): LintingRules {
    val identifierNamingConvention = ruleDtos.first { it.name == "identifierNamingConvention" }.value as? String ?: "snake-case"
    val printlnExpressionAllowed = ruleDtos.first { it.name == "printlnExpressionAllowed" }.isActive
    val readInputExpressionAllowed = ruleDtos.first { it.name == "readInputExpressionAllowed" }.isActive

    return LintingRules(
      identifierNamingConvention = identifierNamingConvention,
      printlnExpressionAllowed = printlnExpressionAllowed,
      readInputExpressionAllowed = readInputExpressionAllowed
    )
  }
}
