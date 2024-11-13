package com.ingsis.snippets.format

import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.async.producer.format.SnippetFormatRequest
import com.ingsis.snippets.rules.Rule
import com.ingsis.snippets.snippet.SnippetService
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/format")
@RestController
class FormatterController(
  private val snippetService: SnippetService,
  private val snippetFormatProducer: SnippetFormatProducer
) {

  private val logger = LoggerFactory.getLogger(FormatterController::class.java)

  @PostMapping("/{id}")
  suspend fun formatSnippet(@PathVariable id: String) {
    logger.info("Formatting snippet with id: $id")

    val snippet = snippetService.getSnippetById(id)

    val snippetToFormat = SnippetFormatRequest(
      container = snippet.author,
      key = snippet.id,
      language = snippet.language,
      version = "1.1"
    )

    snippetFormatProducer.publishEvent(snippetToFormat)

    logger.info("Published formatting request for snippet id: $id")
  }

  @GetMapping("/rules")
  fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): List<Rule> {
    val userId = jwt.subject
    logger.info("Fetching formatting rules for userId: $userId")

    return snippetService.getFormattingRules(userId)
  }
}
