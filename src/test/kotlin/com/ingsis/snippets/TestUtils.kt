package com.ingsis.snippets

import com.ingsis.snippets.snippet.SnippetDto
import org.springframework.test.web.reactive.server.WebTestClient

class TestUtils(val client: WebTestClient) {

  fun createSnippet(request: SnippetDto): WebTestClient.ResponseSpec {
    return client.post().uri("/snippets/create").bodyValue(request)
      .exchange()
  }

  fun getSnippet(snippetId: String?): WebTestClient.ResponseSpec {
    return client.get().uri("/snippets/$snippetId")
      .exchange()
      .expectStatus().isOk
  }

  fun getDeletedSnippet(snippetId: String?): WebTestClient.ResponseSpec {
    return client.get().uri("/snippets/$snippetId")
      .exchange()
      .expectStatus().isNotFound
  }

  fun updateSnippet(snippetId: String?, request: SnippetDto): WebTestClient.ResponseSpec {
    return client.put().uri("/snippets/update/$snippetId").bodyValue(request)
      .exchange()
      .expectStatus().isOk
  }

  fun deleteSnippet(snippetId: String?): WebTestClient.ResponseSpec {
    return client.delete().uri("/snippets/delete/$snippetId")
      .exchange()
      .expectStatus().isOk
  }
}
