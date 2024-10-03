package com.ingsis.snippets.tag

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TagController::class)
class TagControllerTests {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var tagService: TagService

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @Test
  fun `should create a tag`() {
    val tag = Tag(id = "1", name = "Kotlin", snippetId = "snippet1")

    `when`(tagService.createTag(tag)).thenReturn(tag)

    val tagJson = objectMapper.writeValueAsString(tag)

    mockMvc.perform(
      MockMvcRequestBuilders.post("/tags/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson)
    )
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Kotlin"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.snippetId").value("snippet1"))

    verify(tagService).createTag(tag)
  }

  @Test
  fun `should return all tags`() {
    val tags = listOf(
      Tag(id = "1", name = "Java", snippetId = "snippet1"),
      Tag(id = "2", name = "Kotlin", snippetId = "snippet2")
    )

    `when`(tagService.getAllTags()).thenReturn(tags)

    mockMvc.perform(MockMvcRequestBuilders.get("/tags"))
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Java"))
      .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Kotlin"))

    verify(tagService).getAllTags()
  }

  @Test
  fun `should update existing tag`() {
    val existingTag = Tag(id = "1", name = "Java", snippetId = "snippet1")
    val updatedTag = Tag(name = "Kotlin", snippetId = "snippet2")

    `when`(tagService.updateTag("1", updatedTag)).thenReturn(updatedTag)

    val tagJson = objectMapper.writeValueAsString(updatedTag)

    mockMvc.perform(
      MockMvcRequestBuilders.put("/tags/update/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson)
    )
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Kotlin"))

    verify(tagService).updateTag("1", updatedTag)
  }

  @Test
  fun `should delete existing tag`() {
    `when`(tagService.deleteTag("1")).thenReturn(true)

    mockMvc.perform(MockMvcRequestBuilders.delete("/tags/delete/1"))
      .andExpect(MockMvcResultMatchers.status().isOk)

    verify(tagService).deleteTag("1")
  }
}
