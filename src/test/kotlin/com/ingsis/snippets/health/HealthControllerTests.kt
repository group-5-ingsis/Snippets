package com.ingsis.snippets.health

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@WebMvcTest(HealthController::class)
class HealthControllerTests(@Autowired val mockMvc: MockMvc) {

  @Test
  fun `checkHealth should return OK status with a message`() {
    mockMvc.get("/health")
      .andExpect {
        status { isOk() }
        content { string("Service is running") }
      }
  }

  @Test
  fun `sayHello should return OK status with Hello, World! message`() {
    mockMvc.get("/health/hello")
      .andExpect {
        status { isOk() }
        content { string("Hello, World!") }
      }
  }

  @Test
  fun `getServiceInfo should return service info`() {
    mockMvc.get("/health/info")
      .andExpect {
        status { isOk() }
        content { string("Service Name: Parse Service\nVersion: 1.0.0") }
      }
  }

  @Test
  fun `getCurrentTimestamp should return current timestamp`() {
    mockMvc.get("/health/timestamp")
      .andExpect {
        status { isOk() }
      }
  }

  @Test
  fun `getHealthStatus should return health status`() {
    mockMvc.get("/health/health/status")
      .andExpect {
        status { isOk() }
      }
  }

  @Test
  fun `sayGoodbye should return goodbye message`() {
    mockMvc.get("/health/goodbye")
      .andExpect {
        status { isOk() }
        content { string("Goodbye, World!") }
      }
  }
}
