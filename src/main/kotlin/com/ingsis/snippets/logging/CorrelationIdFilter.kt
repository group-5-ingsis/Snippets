package com.ingsis.snippets.logging

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.UUID
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class CorrelationIdFilter : Filter {

  companion object {
    const val CORRELATION_ID_HEADER = "X-Correlation-Id"
  }

  private val logger = LoggerFactory.getLogger(CorrelationIdFilter::class.java)

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpServletRequest = request as HttpServletRequest
    var correlationId = httpServletRequest.getHeader(CORRELATION_ID_HEADER)

    if (correlationId.isNullOrEmpty()) {
      correlationId = UUID.randomUUID().toString() // Generate a new ID if not provided
      logger.info("Generated new Correlation ID: $correlationId")
    } else {
      logger.info("Received Correlation ID: $correlationId")
    }

    MDC.put(CORRELATION_ID_HEADER, correlationId) // Set the ID in MDC for logging

    try {
      chain.doFilter(request, response)
    } finally {
      MDC.remove(CORRELATION_ID_HEADER) // Clear the ID from MDC after request completes
    }
  }

  override fun init(filterConfig: FilterConfig?) {}
  override fun destroy() {}
}
