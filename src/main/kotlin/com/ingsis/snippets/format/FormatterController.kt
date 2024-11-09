package com.ingsis.snippets.format

import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.async.producer.format.SnippetFormatRequest
import com.ingsis.snippets.rules.Rule
import com.ingsis.snippets.snippet.SnippetService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/snippet/format")
@RestController
class FormatterController(
  private val snippetService: SnippetService,
  private val snippetFormatProducer: SnippetFormatProducer
) {

  @PostMapping("/{id}")
  suspend fun formatSnippet(@PathVariable id: String) {
    val snippet = snippetService.getSnippet(id)

    val snippetToFormat = SnippetFormatRequest(
      container = snippet.author,
      key = snippet.id,
      language = snippet.language,
      version = "1.1"
    )

    snippetFormatProducer.publishEvent(snippetToFormat)
  }

  @GetMapping("/rules")
  fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): List<Rule> {
    val userId = jwt.subject
    return snippetService.getFormattingRules(userId)
  }
}
