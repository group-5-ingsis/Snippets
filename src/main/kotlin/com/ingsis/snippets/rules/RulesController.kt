package com.ingsis.snippets.rules

import com.ingsis.snippets.security.JwtInfoExtractor
import com.ingsis.snippets.user.UserData
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RulesController(
  private val rulesService: RulesService
) {

  private val logger = LoggerFactory.getLogger(RulesController::class.java)

  @PostMapping("/format")
  suspend fun formatSnippet(@AuthenticationPrincipal jwt: Jwt, @RequestBody content: String): String {
    val (userId, username) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Formatting snippet for userId: $userId")
    return rulesService.formatSnippet(username, content)
  }

  @PostMapping("/format/rules")
  fun updateFormattingRules(@AuthenticationPrincipal jwt: Jwt, @RequestBody newRuleDtos: List<RuleDto>): List<RuleDto> {
    val (userId, username) = JwtInfoExtractor.extractUserInfo(jwt)
    val userData = UserData(userId, username)
    logger.info("Updating formatting rules for userId: $userId")
    return rulesService.updateFormattingRules(userData, newRuleDtos)
  }

  @PostMapping("/lint/rules")
  fun updateLintingRules(@AuthenticationPrincipal jwt: Jwt, @RequestBody newRuleDtos: List<RuleDto>): List<RuleDto> {
    val (userId, username) = JwtInfoExtractor.extractUserInfo(jwt)
    val userData = UserData(userId, username)
    logger.info("Updating linting rules for userId: $userId")
    return rulesService.updateLintingRules(userData, newRuleDtos)
  }

  @GetMapping("/format/rules")
  fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): List<RuleDto> {
    val (userId, username) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Fetching formatting rules for userId: $userId")
    return rulesService.getFormattingRules(username)
  }

  @GetMapping("/lint/rules")
  fun getLintingRules(@AuthenticationPrincipal jwt: Jwt): List<RuleDto> {
    val (userId, username) = JwtInfoExtractor.extractUserInfo(jwt)
    logger.info("Fetching linting rules for userId: $userId")
    return rulesService.getLintingRules(username)
  }
}
