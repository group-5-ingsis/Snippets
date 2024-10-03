package com.ingsis.snippets.snippet

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class SnippetServiceTests {

  @Mock
  private lateinit var snippetRepository: SnippetRepository

  @InjectMocks
  private lateinit var snippetService: SnippetService

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `createSnippet should save and return snippet`() {
    val snippet = Snippet().apply {
      title = "Test Snippet"
      content = "This is a test snippet."
      language = "Kotlin"
    }

    `when`(snippetRepository.save(snippet)).thenReturn(snippet)

    val createdSnippet = snippetService.createSnippet(snippet)

    assertNotNull(createdSnippet)
    assertEquals("Test Snippet", createdSnippet.title)
  }

  @Test
  fun `getSnippet should return snippet when found`() {
    val snippetId = "12345"
    val snippet = Snippet().apply {
      id = snippetId
      title = "Test Snippet"
    }

    `when`(snippetRepository.findById(snippetId)).thenReturn(Optional.of(snippet))

    val retrievedSnippet = snippetService.getSnippet(snippetId)

    assertNotNull(retrievedSnippet)
    assertEquals(snippetId, retrievedSnippet?.id)
  }

  @Test
  fun `getSnippet should return null when not found`() {
    val snippetId = "12345"
    `when`(snippetRepository.findById(snippetId)).thenReturn(Optional.empty())

    val retrievedSnippet = snippetService.getSnippet(snippetId)

    assertNull(retrievedSnippet)
  }

  @Test
  fun `updateSnippet should return updated snippet when found`() {
    val snippetId = "12345"
    val existingSnippet = Snippet().apply {
      id = snippetId
      title = "Old Title"
      content = "Old Content"
    }
    val updatedSnippet = Snippet().apply {
      title = "New Title"
      content = "New Content"
      language = "Kotlin"
    }

    `when`(snippetRepository.findById(snippetId)).thenReturn(Optional.of(existingSnippet))
    `when`(snippetRepository.save(existingSnippet)).thenReturn(existingSnippet)

    val resultSnippet = snippetService.updateSnippet(snippetId, updatedSnippet)

    assertNotNull(resultSnippet)
    assertEquals("New Title", resultSnippet?.title)
    assertEquals("New Content", resultSnippet?.content)
  }

  @Test
  fun `updateSnippet should return null when snippet not found`() {
    val snippetId = "12345"
    val updatedSnippet = Snippet()

    `when`(snippetRepository.findById(snippetId)).thenReturn(Optional.empty())

    val resultSnippet = snippetService.updateSnippet(snippetId, updatedSnippet)

    assertNull(resultSnippet)
  }

  @Test
  fun `deleteSnippet should return true when snippet exists`() {
    val snippetId = "12345"
    `when`(snippetRepository.existsById(snippetId)).thenReturn(true)

    val result = snippetService.deleteSnippet(snippetId)

    assertTrue(result)
  }

  @Test
  fun `deleteSnippet should return false when snippet does not exist`() {
    val snippetId = "12345"
    `when`(snippetRepository.existsById(snippetId)).thenReturn(false)

    val result = snippetService.deleteSnippet(snippetId)

    assertFalse(result)
  }
}
