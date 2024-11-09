package com.ingsis.snippets.language

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/snippet/language")
@RestController
class LanguageController {

  @GetMapping
  fun getFileTypes(): List<Language> {
    return LanguageProvider.getLanguages()
  }
}
