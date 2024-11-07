package com.ingsis.snippets

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Snippets

fun main(args: Array<String>) {
  val dotenv = Dotenv.load()

  val profile = dotenv["SPRING_PROFILES_ACTIVE"] ?: "local"
  System.setProperty("spring.profiles.active", profile)

  if (profile == "local") {
    System.setProperty("SPRING_DATASOURCE_URL", dotenv["SPRING_DATASOURCE_LOCAL_URL"])
    System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv["SPRING_DATASOURCE_USERNAME"])
    System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv["SPRING_DATASOURCE_PASSWORD"])
    System.setProperty("SERVER_PORT", dotenv["SERVER_PORT"])
    System.setProperty("ASSET_SERVICE_URL", dotenv["ASSET_SERVICE_URL"])
    System.setProperty("AUTH0_AUDIENCE", dotenv["AUTH0_AUDIENCE"])
    System.setProperty("AUTH_SERVER_URI", dotenv["AUTH_SERVER_URI"])
    System.setProperty("AUTH_CLIENT_ID", dotenv["AUTH_CLIENT_ID"])
    System.setProperty("AUTH_CLIENT_SECRET", dotenv["AUTH_CLIENT_SECRET"])
    System.setProperty("FORMAT_STREAM_KEY", dotenv["FORMAT_STREAM_KEY"])
    System.setProperty("GROUP_ID", dotenv["GROUP_ID"])
  }
  runApplication<Snippets>(*args)
}
