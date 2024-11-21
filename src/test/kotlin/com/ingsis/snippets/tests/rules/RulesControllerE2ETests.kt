package com.ingsis.snippets.tests.rules

import com.fasterxml.jackson.databind.ObjectMapper
import com.ingsis.snippets.rules.FORMATTING_KEY
import com.ingsis.snippets.rules.LINTING_KEY
import com.ingsis.snippets.rules.RuleDto
import com.ingsis.snippets.rules.RulesService
import com.ingsis.snippets.user.UserData
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RulesControllerE2ETests @Autowired constructor(
  private val client: WebTestClient,
  private val rulesService: RulesService
) {

  private lateinit var accessToken: String

  @BeforeAll
  fun setup() {
    accessToken = fetchAccessToken()
    val userData = UserData("auth0|6738e1579d3c4beaae5d1487", "whahw")

    // Initialize all formatting rules with correct names and default values
    val initialFormattingRules = listOf(
      RuleDto(id = "1", name = "Space Before Colon", isActive = true),
      RuleDto(id = "2", name = "Space After Colon", isActive = true),
      RuleDto(id = "3", name = "Space Around Assignment", isActive = true),
      RuleDto(id = "4", name = "New line after Println", isActive = true, value = 1),
      RuleDto(id = "5", name = "Block Indentation", isActive = true, value = 4),
      RuleDto(id = "6", name = "If-Brace same line", isActive = true)
    )

    // Initialize all linting rules with correct names and default values
    val initialLintingRules = listOf(
      RuleDto(id = "7", name = "Identifier Naming Convention", isActive = true, value = "camelCase"),
      RuleDto(id = "8", name = "Println Expression Allowed", isActive = false),
      RuleDto(id = "9", name = "Read Input Expression Allowed", isActive = true)
    )

    rulesService.updateRules(userData, initialFormattingRules, FORMATTING_KEY)
    rulesService.updateRules(userData, initialLintingRules, LINTING_KEY)
  }

  private fun fetchAccessToken(): String {
    val process = ProcessBuilder()
      .command(
        "curl",
        "--location",
        System.getenv("AUTH_SERVER_URI"),
        "--data-urlencode", "grant_type=password",
        "--data-urlencode", "username=${System.getenv("AUTH_USERNAME")}",
        "--data-urlencode", "password=${System.getenv("AUTH_PASSWORD")}",
        "--data-urlencode", "scope=${System.getenv("AUTH_SCOPE")}",
        "--data-urlencode", "audience=${System.getenv("AUTH0_AUDIENCE")}",
        "--data-urlencode", "client_id=${System.getenv("AUTH_CLIENT_ID")}",
        "--data-urlencode", "client_secret=${System.getenv("AUTH_CLIENT_SECRET")}"
      )
      .redirectErrorStream(true)
      .start()

    val rawResponse = process.inputStream.bufferedReader().readText()

    val jsonStartIndex = rawResponse.indexOf('{')
    val jsonEndIndex = rawResponse.lastIndexOf('}')
    if (jsonStartIndex == -1 || jsonEndIndex == -1) {
      throw IllegalArgumentException("No valid JSON object found in the response")
    }
    val jsonResponse = rawResponse.substring(jsonStartIndex, jsonEndIndex + 1)

    val tokenResponse = ObjectMapper().readTree(jsonResponse)
    return tokenResponse["access_token"].asText()
  }

  @Test
  fun `should format a snippet`() {
    val content = "let x: number=5;"

    val formattedContent = client.post()
      .uri("/format")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(content)
      .exchange()
      .expectStatus().isOk
      .expectBody(String::class.java)
      .returnResult()
      .responseBody!!

    println(formattedContent)

    assert(formattedContent == "let x : number = 5;\n")
  }

  @Test
  fun `should update all formatting rules`() {
    val newRules = listOf(
      RuleDto(id = "1", name = "Space Before Colon", isActive = false),
      RuleDto(id = "2", name = "Space After Colon", isActive = false),
      RuleDto(id = "3", name = "Space Around Assignment", isActive = false),
      RuleDto(id = "4", name = "New line after Println", isActive = true, value = 2),
      RuleDto(id = "5", name = "Block Indentation", isActive = true, value = 8),
      RuleDto(id = "6", name = "If-Brace same line", isActive = false)
    )

    val updatedRules = client.post()
      .uri("/format/rules")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(newRules)
      .exchange()
      .expectStatus().isOk
      .expectBodyList(RuleDto::class.java)
      .returnResult()
      .responseBody!!

    assert(updatedRules.isNotEmpty())
    assert(updatedRules.any { it.name == "New line after Println" && it.value == 2 })
    assert(updatedRules.any { it.name == "Block Indentation" && it.value == 8 })
  }

  @Test
  fun `should update all linting rules`() {
    val newRules = listOf(
      RuleDto(id = "7", name = "Identifier Naming Convention", isActive = true, value = "snake_case"),
      RuleDto(id = "8", name = "Println Expression Allowed", isActive = true),
      RuleDto(id = "9", name = "Read Input Expression Allowed", isActive = false)
    )

    val updatedRules = client.post()
      .uri("/lint/rules")
      .header("Authorization", "Bearer $accessToken")
      .bodyValue(newRules)
      .exchange()
      .expectStatus().isOk
      .expectBodyList(RuleDto::class.java)
      .returnResult()
      .responseBody!!

    assert(updatedRules.isNotEmpty())
    assert(updatedRules.any { it.name == "Identifier Naming Convention" && it.value == "snake_case" })
    assert(updatedRules.any { it.name == "Println Expression Allowed" && it.isActive })
    assert(updatedRules.any { it.name == "Read Input Expression Allowed" && !it.isActive })
  }

  @Test
  fun `should fetch formatting rules`() {
    val formattingRules = client.get()
      .uri("/format/rules")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(RuleDto::class.java)
      .returnResult()
      .responseBody!!

    assert(formattingRules.isNotEmpty())
    assert(formattingRules.any { it.name == "Space Before Colon" && it.isActive })
  }

  @Test
  fun `should fetch linting rules`() {
    val lintingRules = client.get()
      .uri("/lint/rules")
      .header("Authorization", "Bearer $accessToken")
      .exchange()
      .expectStatus().isOk
      .expectBodyList(RuleDto::class.java)
      .returnResult()
      .responseBody!!

    assert(lintingRules.isNotEmpty())
    assert(lintingRules.any { it.name == "Identifier Naming Convention" && it.value == "camelCase" })
  }
}
