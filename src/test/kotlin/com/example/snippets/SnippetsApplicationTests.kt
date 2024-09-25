package com.example.snippets

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [com.example.snippets.app.SnippetsApplication::class])
class SnippetsApplicationTests {

  @Test
  fun contextLoads() {
  }
}
