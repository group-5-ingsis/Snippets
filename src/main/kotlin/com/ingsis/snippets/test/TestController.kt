package com.ingsis.snippets.test

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class TestController(
  private val testService: TestService
) {
  private val logger = LoggerFactory.getLogger(TestController::class.java)

  @PostMapping("/{snippetId}")
  fun createTest(@RequestBody test: TestDto, @PathVariable snippetId: String): TestDto {
    logger.info("Received request to create a new test for snippet id: $snippetId")
    return testService.createTest(snippetId, test)
  }

  @DeleteMapping("/{id}")
  fun removeTest(@PathVariable id: String) {
    logger.info("Received request to remove test for snippet id: $id")
  }

  @GetMapping("/{snippetId}")
  fun getAllTestsForSnippet(@PathVariable snippetId: String): List<TestDto> {
    logger.info("Received request to get all tests for snippet with id: $snippetId")
    return testService.getAllTestsForSnippet(snippetId)
  }

  @PostMapping("/run/{testId}")
  fun testSnippet(@PathVariable testId: String) {
    logger.info("Received request to run test with id: $testId")
    testService.runTest(testId)
  }

  @PostMapping("/run/{snippetId}/all")
  fun runAllTestsForSnippet(@PathVariable snippetId: String) {
    logger.info("Received request to run all tests for snippet with id: $snippetId")
    testService.runAllTests(snippetId)
  }
}
