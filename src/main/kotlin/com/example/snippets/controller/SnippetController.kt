package com.example.snippets.controller

import com.example.snippets.entity.Snippet
import com.example.snippets.service.SnippetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/snippets")
class SnippetController(@Autowired private val snippetService: SnippetService) {



  @GetMapping
  fun getAllSnippets(): List<Snippet> {
    return snippetService.getAllSnippets()
  }

  @GetMapping("/title/{title}")
  fun getSnippetByTitle(@PathVariable title: String): List<Snippet> {
    return snippetService.getSnippetByTitle(title)
  }
}
