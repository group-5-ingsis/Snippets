package com.ingsis.snippets.rules

import com.ingsis.snippets.async.producer.format.FormatRequest
import com.ingsis.snippets.async.producer.format.FormattedSnippetConsumer
import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.async.producer.lint.LintRequest
import com.ingsis.snippets.async.producer.lint.LintRequestProducer
import com.ingsis.snippets.async.producer.lint.LintResultConsumer
import com.ingsis.snippets.snippet.SnippetService
import com.ingsis.snippets.snippet.UserData
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class RulesController(
  private val snippetService: SnippetService,
  private val snippetFormatProducer: SnippetFormatProducer,
  private val formattedSnippetConsumer: FormattedSnippetConsumer,
  private val lintRequestProducer: LintRequestProducer,
  private val lintResultConsumer: LintResultConsumer
) {

  private val logger = LoggerFactory.getLogger(RulesController::class.java)

  @PostMapping("/format")
  suspend fun formatSnippet(@AuthenticationPrincipal jwt: Jwt, @RequestBody content: String): String {
    val (_, username) = extractUserInfo(jwt)
    val requestId = UUID.randomUUID().toString()
    val formatRequest = FormatRequest(requestId, username, snippet = content)

    snippetFormatProducer.publishEvent(formatRequest)

    val responseDeferred = formattedSnippetConsumer.getFormatResponse(requestId)
    return responseDeferred.await()
  }

  @PostMapping("/lint")
  suspend fun lintSnippet(@AuthenticationPrincipal jwt: Jwt, @RequestBody content: String): String {
    val (_, username) = extractUserInfo(jwt)
    val requestId = UUID.randomUUID().toString()
    val lintRequest = LintRequest(requestId, username, snippet = content)

    lintRequestProducer.publishEvent(lintRequest)

    val responseDeferred = lintResultConsumer.getFormatResponse(requestId)
    return responseDeferred.await()
  }

  @GetMapping("/format/rules")
  fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): List<Rule> {
    val (userId, username) = extractUserInfo(jwt)
    logger.info("Fetching formatting rules for userId: $userId")

    return snippetService.getFormattingRules(username)
  }

  @PostMapping("/format/rules")
  fun updateFormattingRules(@AuthenticationPrincipal jwt: Jwt, @RequestBody newRules: List<Rule>): List<Rule> {
    val (userId, username) = extractUserInfo(jwt)
    val userData = UserData(userId, username)
    return snippetService.updateFormattingRules(userData, newRules)
  }

//  @PostMapping("/lint/{id}")
//  suspend fun lintSnippet(@PathVariable id: String) {
//    logger.info("Linting snippet with id: $id")
//
//    val snippet = snippetService.getSnippetById(id)
//
//    val snippetToFormat = SnippetFormatRequest(
//      container = snippet.author,
//      key = snippet.id,
//      language = snippet.language,
//      version = "1.1"
//    )
//
//    //snippetFormatProducer.publishEvent(snippetToFormat)
//
//    logger.info("Published formatting request for snippet id: $id")
//  }

  @GetMapping("/lint/rules")
  fun getLintingRules(@AuthenticationPrincipal jwt: Jwt): List<Rule> {
    val (userId, username) = extractUserInfo(jwt)
    logger.info("Fetching linting rules for userId: $userId")
    return snippetService.getLintingRules(username)
  }

  private fun extractUserInfo(jwt: Jwt): Pair<String, String> {
    val userId = jwt.subject
    val username = jwt.claims["https://snippets/claims/username"]?.toString() ?: "unknown"
    return Pair(userId, username)
  }
}
