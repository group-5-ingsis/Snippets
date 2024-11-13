package com.ingsis.snippets.logging

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import com.newrelic.api.agent.NewRelic  // Import New Relic API
import java.util.UUID

@Component
class CorrelationIdFilter : Filter {

  companion object {
    const val CORRELATION_ID_HEADER = "X-Correlation-ID"
  }

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpServletRequest = request as HttpServletRequest
    val httpServletResponse = response as HttpServletResponse

    // Get or generate a correlation ID
    val correlationId = httpServletRequest.getHeader(CORRELATION_ID_HEADER) ?: UUID.randomUUID().toString()

    // Add the correlation ID to the response header
    httpServletResponse.setHeader(CORRELATION_ID_HEADER, correlationId)

    // Set the correlation ID in the MDC context for logging
    MDC.put(CORRELATION_ID_HEADER, correlationId)

    // Add correlation ID as a custom attribute in New Relic
    NewRelic.addCustomParameter("Correlation-ID", correlationId)

    try {
      chain.doFilter(request, response)
    } finally {
      MDC.remove(CORRELATION_ID_HEADER)
    }
  }

  override fun init(filterConfig: FilterConfig?) {}
  override fun destroy() {}
}
