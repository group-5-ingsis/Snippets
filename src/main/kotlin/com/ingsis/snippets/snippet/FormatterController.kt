package com.ingsis.snippets.snippet

import com.ingsis.snippets.async.producer.format.SnippetFormatProducer
import com.ingsis.snippets.async.producer.format.SnippetFormatRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/snippets/format")
@RestController
class FormatterController(
  private val snippetService: SnippetService,
  private val snippetFormatProducer: SnippetFormatProducer
) {

  @PostMapping("/format/{id}")
  suspend fun formatSnippet(@PathVariable id: String) {
    val snippet = snippetService.getSnippet(id)

    val snippetToFormat = SnippetFormatRequest(
      container = snippet.author,
      key = snippet.id,
      language = snippet.language,
      version = snippet.version
    )

    snippetFormatProducer.publishEvent(snippetToFormat)
  }

  @GetMapping("/format/rules")
  fun getFormattingRules(@AuthenticationPrincipal jwt: Jwt): String {
    val userId = jwt.subject
    return snippetService.getFormattingRules(userId)
  }
}
