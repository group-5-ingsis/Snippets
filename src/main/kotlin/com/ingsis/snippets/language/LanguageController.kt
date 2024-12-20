package com.ingsis.snippets.language

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/language")
@RestController
class LanguageController {

  @GetMapping("/types")
  fun getFileTypes(): List<Language> {
    return LanguageProvider.getLanguages()
  }
}
