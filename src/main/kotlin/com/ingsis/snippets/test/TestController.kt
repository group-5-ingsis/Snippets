package com.ingsis.snippets.test

import com.ingsis.snippets.async.producer.test.CreateTestDto
import com.ingsis.snippets.async.producer.test.SnippetCreateTestRequest
import com.ingsis.snippets.async.producer.test.SnippetTestProducer
import com.ingsis.snippets.async.producer.test.SnippetTestRequest
import com.ingsis.snippets.snippet.SnippetService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/test")
class TestController(
  private val snippetService: SnippetService,
  private val snippetTestProducer: SnippetTestProducer
) {
  private val logger = LoggerFactory.getLogger(TestController::class.java)

  @PostMapping("/")
  suspend fun createSnippet(@RequestBody snippetTest: CreateTestDto) {
    val snippet = snippetService.getSnippetById(snippetTest.snippetId)
    val languageAndVersion = snippet.language.split(" ")
    snippetTestProducer.publishCreateTestEvent(
      SnippetCreateTestRequest(snippetTest, languageAndVersion[0], languageAndVersion[1]))
    logger.info("Sent request to create a new test")
  }

  @PostMapping("/{id}")
  suspend fun testSnippet(@PathVariable id: String) {
    val snippet = snippetService.getSnippetById(id)
    val snippetRequest = SnippetTestRequest(
      snippet.author,
      snippet.id,
    )
    snippetTestProducer.publishTestRequestEvent(snippetRequest)
    logger.info("Published test request for snippet id: $id")
  }
}
