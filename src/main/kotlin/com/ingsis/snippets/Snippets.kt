package com.ingsis.snippets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Snippets

fun main(args: Array<String>) {
  runApplication<Snippets>(*args)
}
