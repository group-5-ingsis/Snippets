package com.ingsis.snippets

import com.ingsis.snippets.snippet.SnippetDto
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.Date

class TestUtils(val client: WebTestClient) {

  private val issuer = "https://your-auth0-domain/"
  private val audience = "your-audience"
  private val secret = "test-secret"

  private val mockJwt: String = generateMockJwt(audience, issuer, secret)

  fun getMockJwt(): String {
    return mockJwt
  }

  fun generateMockJwt(audience: String, issuer: String, secret: String): String {
    val now = Date()
    val expiry = Date(now.time + 3600000)

    return Jwts.builder()
      .setSubject("test-user")
      .setIssuer(issuer)
      .setAudience(audience)
      .setIssuedAt(now)
      .setExpiration(expiry)
      .claim("scope", "read:snippets write:snippets")
      .signWith(SignatureAlgorithm.HS256, secret)
      .compact()
  }

  fun createSnippet(request: SnippetDto): WebTestClient.ResponseSpec {
    return client.post()
      .uri("/snippets/create")
      .header("Authorization", "Bearer $mockJwt")
      .bodyValue(request)
      .exchange()
  }

  fun getSnippet(snippetId: String?): WebTestClient.ResponseSpec {
    return client.get()
      .uri("/snippets/$snippetId")
      .header("Authorization", "Bearer $mockJwt")
      .exchange()
      .expectStatus().isOk
  }

  fun getDeletedSnippet(snippetId: String?): WebTestClient.ResponseSpec {
    return client.get()
      .uri("/snippets/$snippetId")
      .header("Authorization", "Bearer $mockJwt")
      .exchange()
      .expectStatus().isNotFound
  }

  fun updateSnippet(snippetId: String?, request: SnippetDto): WebTestClient.ResponseSpec {
    return client.put()
      .uri("/snippets/update/$snippetId")
      .header("Authorization", "Bearer $mockJwt")
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
  }

  fun deleteSnippet(snippetId: String?): WebTestClient.ResponseSpec {
    return client.delete()
      .uri("/snippets/delete/$snippetId")
      .header("Authorization", "Bearer $mockJwt")
      .exchange()
      .expectStatus().isOk
  }
}
