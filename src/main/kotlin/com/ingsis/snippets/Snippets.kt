package com.ingsis.snippets

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Snippets

fun main(args: Array<String>) {

  val dotenv = Dotenv.load()

  System.setProperty("AUTH_SERVER_URI", dotenv["AUTH_SERVER_URI"])
  System.setProperty("AUTH_CLIENT_ID", dotenv["AUTH_CLIENT_ID"])
  System.setProperty("AUTH_CLIENT_SECRET", dotenv["AUTH_CLIENT_SECRET"])
  System.setProperty("AUTH0_AUDIENCE", dotenv["AUTH0_AUDIENCE"])
  System.setProperty("SERVER_PORT", dotenv["SERVER_PORT"])
  System.setProperty("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5432/snippet")
  System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv["SPRING_DATASOURCE_USERNAME"])
  System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv["SPRING_DATASOURCE_PASSWORD"])

  runApplication<Snippets>(*args)
}
