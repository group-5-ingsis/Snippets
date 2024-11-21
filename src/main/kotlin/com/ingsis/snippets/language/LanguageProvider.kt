package com.ingsis.snippets.language

object LanguageProvider {

  fun getLanguages(): List<Language> {
    val list = mutableListOf<Language>()
    list.add(Language("PrintScript 1.0", "ps"))
    list.add(Language("PrintScript 1.1", "ps"))
    return list
  }
}
