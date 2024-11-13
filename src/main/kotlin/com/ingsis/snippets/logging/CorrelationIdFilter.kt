package com.ingsis.snippets.logging

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

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpServletRequest = request as HttpServletRequest
    var correlationId = httpServletRequest.getHeader(CORRELATION_ID_HEADER)

    if (correlationId.isNullOrEmpty()) {
      correlationId = UUID.randomUUID().toString() // Genera un nuevo ID si no existe
    }

    MDC.put(CORRELATION_ID_HEADER, correlationId) // Agrega el ID al contexto de log

    try {
      chain.doFilter(request, response)
    } finally {
      MDC.remove(CORRELATION_ID_HEADER) // Limpia el ID del contexto después de la respuesta
    }
  }

  override fun init(filterConfig: FilterConfig?) {}
  override fun destroy() {}
}
