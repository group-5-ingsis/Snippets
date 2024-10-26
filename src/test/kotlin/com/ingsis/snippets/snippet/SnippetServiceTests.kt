package com.ingsis.snippets.snippet

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.Optional

class SnippetServiceTests {

  @Mock
  private lateinit var snippetRepository: SnippetRepository

  @Mock
  private lateinit var restTemplate: RestTemplate

  @InjectMocks
  private lateinit var snippetService: SnippetService

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `createSnippet should return true when snippet is created successfully`() {
    val snippetDto = SnippetDto(container = "container1", key = "key1")
    val responseEntity = ResponseEntity(snippetDto, HttpStatus.CREATED)

    `when`(restTemplate.postForEntity(anyString(), eq(snippetDto), eq(SnippetDto::class.java)))
      .thenReturn(responseEntity)

    val result = snippetService.createSnippet(snippetDto)

    assertTrue(result)
    verify(restTemplate, times(1)).postForEntity(anyString(), eq(snippetDto), eq(SnippetDto::class.java))
  }

  @Test
  fun `createSnippet should return false when HttpClientErrorException is thrown`() {
    val snippetDto = SnippetDto(container = "container1", key = "key1")

    `when`(restTemplate.postForEntity(anyString(), eq(snippetDto), eq(SnippetDto::class.java)))
      .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

    val result = snippetService.createSnippet(snippetDto)

    assertFalse(result)
    verify(restTemplate, times(1)).postForEntity(anyString(), eq(snippetDto), eq(SnippetDto::class.java))
  }

  @Test
  fun `getSnippet should return Snippet when found`() {
    val snippet = Snippet(id = "1", language = "Kotlin")
    `when`(snippetRepository.findById("1")).thenReturn(Optional.of(snippet))

    val result = snippetService.getSnippet("1")

    assertNotNull(result)
    assertEquals("Kotlin", result?.language)
  }

  @Test
  fun `getSnippet should return null when not found`() {
    `when`(snippetRepository.findById("1")).thenReturn(Optional.empty())

    val result = snippetService.getSnippet("1")

    assertNull(result)
  }
}
