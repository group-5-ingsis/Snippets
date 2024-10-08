package com.ingsis.snippets.health

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class HealthE2eTests @Autowired constructor(
  val client: WebTestClient
) {

  @Test
  fun `checkHealth should return OK status with a message`() {
    client.get().uri("/health").exchange()
      .expectStatus().isOk
      .expectBody(String::class.java).isEqualTo("Service is running")
  }

  @Test
  fun `sayHello should return OK status with Hello message`() {
    client.get().uri("/health/hello").exchange()
      .expectStatus().isOk
      .expectBody(String::class.java).isEqualTo("Hello, World!")
  }

  @Test
  fun `getServiceInfo should return OK status with service info`() {
    client.get().uri("/health/info").exchange()
      .expectStatus().isOk
      .expectBody(String::class.java).isEqualTo("Service Name: Parse Service\nVersion: 1.0.0")
  }

  @Test
  fun `getCurrentTimestamp should return OK status with a valid timestamp`() {
    client.get().uri("/health/timestamp").exchange()
      .expectStatus().isOk
      .expectBody(String::class.java).consumeWith {
        val timestamp = it.responseBody!!
        kotlin.test.assertTrue(timestamp.matches(Regex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")))
      }
  }

  @Test
  fun `getHealthStatus should return OK status with health status and uptime`() {
    client.get().uri("/health/health/status").exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$.status").isEqualTo("UP")
      .jsonPath("$.uptime").exists()
      .jsonPath("$.memory").exists()
  }

  @Test
  fun `sayGoodbye should return OK status with Goodbye message`() {
    client.get().uri("/health/goodbye").exchange()
      .expectStatus().isOk
      .expectBody(String::class.java).isEqualTo("Goodbye, World!")
  }
}
