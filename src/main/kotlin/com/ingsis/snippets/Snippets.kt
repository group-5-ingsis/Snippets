package com.ingsis.snippets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
open class Snippets {

  @Bean
  open fun restTemplate(): RestTemplate {
    return RestTemplate()
  }

  fun main(args: Array<String>) {
    runApplication<Snippets>(*args)
  }
}
