package com.ingsis.snippets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
open class Snippets

fun main(args: Array<String>) {
  runApplication<Snippets>(*args)
}
