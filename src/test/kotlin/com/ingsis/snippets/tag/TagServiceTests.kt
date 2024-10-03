package com.ingsis.snippets.tag

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TagServiceTests {

  @Mock
  private lateinit var tagRepository: TagRepository

  @InjectMocks
  private lateinit var tagService: TagService

  @BeforeEach
  fun setUp() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should create tag`() {
    val tag = Tag(name = "Kotlin", snippetId = "snippet1")
    whenever(tagRepository.save(tag)).thenReturn(tag)

    val createdTag = tagService.createTag(tag)

    assertEquals(tag.name, createdTag.name)
    assertEquals(tag.snippetId, createdTag.snippetId)
  }

  @Test
  fun `should return all tags`() {
    val tags = listOf(
      Tag(id = "1", name = "Java", snippetId = "snippet1"),
      Tag(id = "2", name = "Kotlin", snippetId = "snippet2")
    )
    whenever(tagRepository.findAll()).thenReturn(tags)

    val result = tagService.getAllTags()

    assertEquals(2, result.size)
  }

  @Test
  fun `should update existing tag`() {
    val existingTag = Tag(id = "1", name = "Java", snippetId = "snippet1")
    val updatedTag = Tag(name = "Kotlin", snippetId = "snippet2")
    whenever(tagRepository.findById("1")).thenReturn(java.util.Optional.of(existingTag))
    whenever(tagRepository.save(existingTag)).thenReturn(existingTag)

    val result = tagService.updateTag("1", updatedTag)

    assertNotNull(result)
    assertEquals("Kotlin", result?.name)
    assertEquals("snippet2", result?.snippetId)
  }

  @Test
  fun `should delete existing tag`() {
    whenever(tagRepository.existsById("1")).thenReturn(true)

    val result = tagService.deleteTag("1")

    assertTrue(result)
    verify(tagRepository).deleteById("1")
  }
}
