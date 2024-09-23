package com.example.snippets.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController("/api")
class TestController {

  @GetMapping("/helloworld")
  fun getHelloWorld() = "Hello World"

  @GetMapping("/send-message")
  fun sendMessageToPermissionServer(): String {
    val restTemplate = RestTemplate()
    val serverBUrl = "http://permission-app:8083/receive-message"
    val message = "Hello from Snippets!"

    val response = restTemplate.postForObject(serverBUrl, message, String::class.java)

    return "Snippets server sent message: '$message' and received response: '$response'"
  }
}