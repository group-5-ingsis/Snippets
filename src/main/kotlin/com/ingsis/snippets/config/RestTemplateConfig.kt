package com.ingsis.snippets.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import com.ingsis.snippets.logging.CorrelationIdInterceptor // Adjust this import based on your package structure

@Configuration
open class RestTemplateConfig {

  @Bean
  open fun restTemplate(restTemplateBuilder: RestTemplateBuilder,
                        correlationIdInterceptor: CorrelationIdInterceptor): RestTemplate {
    return restTemplateBuilder
      .additionalInterceptors(correlationIdInterceptor)
      .build()
  }
}
