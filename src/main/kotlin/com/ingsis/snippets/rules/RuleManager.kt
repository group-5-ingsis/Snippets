package com.ingsis.snippets.rules

object RuleManager {

  fun getDefaultRules(type: String): Rules {
    return when (type) {
      FORMATTING_KEY -> getDefaultFormattingRules()
      LINTING_KEY -> getDefaultLintingRules()
      else -> throw IllegalArgumentException("Unknown rules type: $type")
    }
  }

  fun convertToRuleList(rules: Rules): List<RuleDto> {
    return when (rules) {
      is FormattingRules -> {
        listOf(
          RuleDto(id = "1", name = "Space Before Colon", isActive = rules.spaceBeforeColon, value = null),
          RuleDto(id = "2", name = "Space After Colon", isActive = rules.spaceAfterColon, value = null),
          RuleDto(id = "3", name = "Space Around Assignment", isActive = rules.spaceAroundAssignment, value = null),
          RuleDto(id = "4", name = "New line after Println", isActive = true, value = rules.newlineAfterPrintln),
          RuleDto(id = "5", name = "Block Indentation", isActive = true, value = rules.blockIndentation),
          RuleDto(id = "6", name = "If-Brace same line", isActive = rules.ifBraceSameLine, value = null)
        )
      }
      is LintingRules -> {
        listOf(
          RuleDto(id = "1", name = "Identifier Naming Convention", isActive = true, value = rules.identifierNamingConvention),
          RuleDto(id = "2", name = "Println Expression Allowed", isActive = rules.printlnExpressionAllowed, value = null),
          RuleDto(id = "3", name = "Read Input Expression Allowed", isActive = rules.readInputExpressionAllowed, value = null)
        )
      }
    }
  }

  fun convertToType(ruleDtos: List<RuleDto>, type: String): Rules {
    return when (type) {
      FORMATTING_KEY -> convertToFormattingRules(ruleDtos)
      LINTING_KEY -> convertToLintingRules(ruleDtos)
      else -> throw IllegalArgumentException("Unknown rules type: $type")
    }
  }

  private fun convertToFormattingRules(ruleDtos: List<RuleDto>): FormattingRules {
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

  private fun convertToLintingRules(ruleDtos: List<RuleDto>): LintingRules {
    val identifierNamingConvention = ruleDtos.first { it.name == "identifierNamingConvention" }.value as? String ?: "snake-case"
    val printlnExpressionAllowed = ruleDtos.first { it.name == "printlnExpressionAllowed" }.isActive
    val readInputExpressionAllowed = ruleDtos.first { it.name == "readInputExpressionAllowed" }.isActive

    return LintingRules(
      identifierNamingConvention = identifierNamingConvention,
      printlnExpressionAllowed = printlnExpressionAllowed,
      readInputExpressionAllowed = readInputExpressionAllowed
    )
  }

  private fun getDefaultFormattingRules(): FormattingRules {
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

  private fun getDefaultLintingRules(): LintingRules {
    val defaultLintingRules = LintingRules(
      identifierNamingConvention = "snake-case",
      printlnExpressionAllowed = false,
      readInputExpressionAllowed = false
    )
    return defaultLintingRules
  }
}
