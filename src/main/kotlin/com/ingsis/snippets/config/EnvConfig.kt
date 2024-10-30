package com.ingsis.snippets.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EnvConfig {
  @Bean
  open fun dotenv(): Dotenv {
    return Dotenv.configure().load()
  }
}
