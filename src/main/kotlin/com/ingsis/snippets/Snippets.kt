package com.ingsis.snippets

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Snippets

fun main(args: Array<String>) {
  val dotenv = try {
    Dotenv.load()
  } catch (e: Exception) {
    println("Error loading .env file: ${e.message}")
    null
  }

  if (dotenv == null) {
    println("Using default environment values as .env file was not found.")
  } else {
    val profile = dotenv["SPRING_PROFILES_ACTIVE"]

    if (profile == "local") {
      System.setProperty("SPRING_DATASOURCE_URL", dotenv["SPRING_DATASOURCE_LOCAL_URL"] ?: "default_local_url")
      System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv["SPRING_DATASOURCE_USERNAME"] ?: "default_username")
      System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv["SPRING_DATASOURCE_PASSWORD"] ?: "default_password")
      System.setProperty("SERVER_PORT", dotenv["SERVER_PORT"] ?: "8080")
      System.setProperty("ASSET_SERVICE_URL", dotenv["ASSET_SERVICE_URL"] ?: "http://localhost:8081")
      System.setProperty("AUTH0_AUDIENCE", dotenv["AUTH0_AUDIENCE"] ?: "default_audience")
      System.setProperty("AUTH_SERVER_URI", dotenv["AUTH_SERVER_URI"] ?: "http://localhost:8082")
      System.setProperty("AUTH_CLIENT_ID", dotenv["AUTH_CLIENT_ID"] ?: "default_client_id")
      System.setProperty("AUTH_CLIENT_SECRET", dotenv["AUTH_CLIENT_SECRET"] ?: "default_client_secret")
      System.setProperty("FORMAT_STREAM_KEY", dotenv["FORMAT_STREAM_KEY"] ?: "default_key")
      System.setProperty("GROUP_ID", dotenv["GROUP_ID"] ?: "default_group")
    }
  }

  runApplication<Snippets>(*args)
}
