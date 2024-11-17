package com.ingsis.snippets.test

import com.ingsis.snippets.snippet.SnippetService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
  private val snippetService: SnippetService
) {
  private val logger = LoggerFactory.getLogger(TestController::class.java)

  @PostMapping("/create")
  fun createSnippet(@RequestBody snippetTest: CreateTestDto) {
    logger.info("Received request to create a new test for snippet id: ${snippetTest.snippetId}")
    snippetService.createTestForSnippet(snippetTest)
  }

  @PostMapping("/run/{testId}")
  suspend fun testSnippet(@PathVariable testId: String) {
    logger.info("Received request to run test with id: $testId")
    snippetService.testSnippet(testId)
  }

  @PostMapping("/run/{snippetId}/all")
  suspend fun runAllTestsForSnippet(@PathVariable snippetId: String) {
    logger.info("Received request to run all tests for snippet with id: $snippetId")
    snippetService.runAllTestsForSnippet(snippetId)
  }
}
