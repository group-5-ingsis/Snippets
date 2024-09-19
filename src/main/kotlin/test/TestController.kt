package test

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api")
class TestController {

  @GetMapping("/helloworld")
  fun getHelloWorld() = "Hello World"
}