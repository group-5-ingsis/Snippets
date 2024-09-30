package com.example.snippets.service

import SnippetRepository
import com.example.snippets.entity.Snippet
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.stereotype.Service

@Service
class SnippetService(
  @Autowired private val snippetRepository: SnippetRepository
) {
  fun getAllSnippets(): List<Snippet> {
    return snippetRepository.findAll()
  }

  fun getSnippetByTitle(title: String): List<Snippet> {
    return snippetRepository.findByTitle(title)
  }

}
