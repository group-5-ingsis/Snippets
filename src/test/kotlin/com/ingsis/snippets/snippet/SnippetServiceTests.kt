package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.AssetClient
import com.ingsis.snippets.snippet.model.SnippetDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.web.client.RestTemplate

class SnippetServiceTests {

  @InjectMocks
  private lateinit var snippetService: SnippetService

  @Mock
  private lateinit var snippetRepository: SnippetRepository

  @Mock
  private lateinit var assetClient: AssetClient

  @Mock
  private lateinit var restTemplate: RestTemplate

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should return Snippet created successfully when snippet is created`() {
    val snippetDto = SnippetDto(container = "PrintScript", key = "HelloWorld.ps", content = "HelloWorld!")

    `when`(assetClient.createOrUpdateSnippet("PrintScript", "HelloWorld.ps", snippetDto))
      .thenReturn("Snippet created successfully.")

    val result = snippetService.createSnippet(snippetDto)

    assertEquals("Snippet created successfully.", result)
    verify(assetClient, times(1)).createOrUpdateSnippet("PrintScript", "HelloWorld.ps", snippetDto)
  }

  @Test
  fun `createOrUpdateSnippet should return Snippet updated successfully when snippet is updated`() {
    val snippetDto = SnippetDto(container = "PrintScript", key = "HelloWorld.ps", content = "HelloWorld!")

    `when`(assetClient.createOrUpdateSnippet("PrintScript", "HelloWorld.ps", snippetDto))
      .thenReturn("Snippet updated successfully.")

    val result = snippetService.createSnippet(snippetDto)

    assertEquals("Snippet updated successfully.", result)
    verify(assetClient, times(1)).createOrUpdateSnippet("PrintScript", "HelloWorld.ps", snippetDto)
  }
}
