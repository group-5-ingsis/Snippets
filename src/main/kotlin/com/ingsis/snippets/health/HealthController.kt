package com.ingsis.snippets.health

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController {

  @GetMapping("/info")
  fun getServiceInfo(): ResponseEntity<String> {
    val serviceInfo = "Service Name: Parse Service"
    return ResponseEntity(serviceInfo, HttpStatus.OK)
  }

  @GetMapping("/jwt")
  fun jwt(@AuthenticationPrincipal jwt: Jwt): String {
    return jwt.tokenValue
  }
}
