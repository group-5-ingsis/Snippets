package com.ingsis.snippets.health

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/health")
class HealthController {

  @GetMapping
  fun checkHealth(): ResponseEntity<String> {
    return ResponseEntity("Service is running", HttpStatus.OK)
  }

  @GetMapping("/test")
  fun test(): ResponseEntity<String> {
    return ResponseEntity("SpringBoot 'test' endpoint working. ", HttpStatus.OK)
  }

  @GetMapping("/hello")
  fun sayHello(): ResponseEntity<String> {
    return ResponseEntity("Hello, World!", HttpStatus.OK)
  }

  @GetMapping("/info")
  fun getServiceInfo(): ResponseEntity<String> {
    val serviceInfo = "Service Name: Parse Service\nVersion: 1.0.0"
    return ResponseEntity(serviceInfo, HttpStatus.OK)
  }

  @GetMapping("/timestamp")
  fun getCurrentTimestamp(): ResponseEntity<String> {
    val currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
    return ResponseEntity(currentTimestamp, HttpStatus.OK)
  }

  @GetMapping("/health/status")
  fun getHealthStatus(): ResponseEntity<Map<String, Any>> {
    val healthStatus = mapOf(
      "status" to "UP",
      "uptime" to "${System.currentTimeMillis() - startTime} ms",
      "memory" to "${Runtime.getRuntime().totalMemory() / 1024} KB"
    )
    return ResponseEntity(healthStatus, HttpStatus.OK)
  }

  @GetMapping("/goodbye")
  fun sayGoodbye(): ResponseEntity<String> {
    return ResponseEntity("Goodbye, World!", HttpStatus.OK)
  }

  companion object {
    private val startTime = System.currentTimeMillis()
  }
}
