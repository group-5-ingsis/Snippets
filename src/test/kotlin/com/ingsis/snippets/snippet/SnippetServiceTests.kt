package com.ingsis.snippets.snippet

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime
import java.util.*

class SnippetServiceTests {

  @Mock
  private lateinit var snippetRepository: SnippetRepository

  @InjectMocks
  private lateinit var snippetService: SnippetService

  private lateinit var snippet: Snippet

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    snippet = Snippet(
      id = "1",
      language = "Kotlin",
      creationDate = LocalDateTime.now(),
      modificationDate = LocalDateTime.now(),
      testCases = listOf("TestCase1")
    )
  }

  @Test
  fun `getSnippet should return snippet when found`() {
    `when`(snippetRepository.findById("1")).thenReturn(Optional.of(snippet))

    val result = snippetService.getSnippet("1")

    assertNotNull(result)
    assertEquals("Kotlin", result?.language)
    verify(snippetRepository, times(1)).findById("1")
  }

  @Test
  fun `getSnippet should return null when snippet not found`() {
    `when`(snippetRepository.findById("1")).thenReturn(Optional.empty())

    val result = snippetService.getSnippet("1")

    assertNull(result)
    verify(snippetRepository, times(1)).findById("1")
  }

  @Test
  fun `updateSnippet should return null when snippet not found`() {
    val updatedSnippet = Snippet(
      id = "1",
      language = "Java",
      creationDate = snippet.creationDate,
      modificationDate = LocalDateTime.now(),
      testCases = listOf("TestCase2")
    )

    `when`(snippetRepository.findById("1")).thenReturn(Optional.empty())

    val result = snippetService.updateSnippet("1", updatedSnippet)

    assertNull(result)
    verify(snippetRepository, times(1)).findById("1")
    verify(snippetRepository, times(0)).save(any(Snippet::class.java))
  }

  @Test
  fun `deleteSnippet should delete and return true when snippet exists`() {
    `when`(snippetRepository.existsById("1")).thenReturn(true)

    val result = snippetService.deleteSnippet("1")

    assertTrue(result)
    verify(snippetRepository, times(1)).existsById("1")
    verify(snippetRepository, times(1)).deleteById("1")
  }

  @Test
  fun `deleteSnippet should return false when snippet does not exist`() {
    `when`(snippetRepository.existsById("1")).thenReturn(false)

    val result = snippetService.deleteSnippet("1")

    assertFalse(result)
    verify(snippetRepository, times(1)).existsById("1")
    verify(snippetRepository, times(0)).deleteById("1")
  }
}
