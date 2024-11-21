package com.ingsis.snippets.logging

import org.slf4j.MDC
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CorrelationIdInterceptor : ClientHttpRequestInterceptor {

  companion object {
    const val CORRELATION_ID_HEADER = "X-Correlation-ID"
  }

  override fun intercept(request: org.springframework.http.HttpRequest, body: ByteArray, execution: org.springframework.http.client.ClientHttpRequestExecution): org.springframework.http.client.ClientHttpResponse {
    val correlationId = MDC.get(CORRELATION_ID_HEADER) ?: UUID.randomUUID().toString()
    request.headers.set(CORRELATION_ID_HEADER, correlationId)
    return execution.execute(request, body)
  }
}
