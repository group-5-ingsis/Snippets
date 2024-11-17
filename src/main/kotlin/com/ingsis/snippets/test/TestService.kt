package com.ingsis.snippets.test

import com.ingsis.snippets.async.test.SnippetCreateTestRequest
import com.ingsis.snippets.snippet.SnippetService
import com.ingsis.snippets.snippet.TestService
import org.springframework.stereotype.Service

@Service
class TestService(private val snippetService: SnippetService, private val testService: TestService) {

  fun createTestForSnippet(snippetId: String, testDto: TestDto): TestDto {
    val snippet = snippetService.getSnippetById(snippetId)
    val languageAndVersion = snippet.language.split(" ")
    val createTestRequest = SnippetCreateTestRequest(
      testDto,
      snippet.author,
      languageAndVersion[0],
      languageAndVersion[1]
    )
    return testService.createTest(createTestRequest)
  }

  fun testSnippet(testId: String) {
    testService.runTest(testId)
  }

  fun runAllTestsForSnippet(snippetId: String) {
    testService.runAllTests(snippetId)
  }
}
