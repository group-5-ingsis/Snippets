package com.ingsis.snippets.tag

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTests {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @MockBean
  private lateinit var tagService: TagService

  @Test
  fun `should create a tag`() {
    val tag = Tag(id = "1", name = "Java", snippetId = "snippet1")

    Mockito.`when`(tagService.createTag(Mockito.any(Tag::class.java))).thenReturn(tag)

    val tagJson = objectMapper.writeValueAsString(tag)

    mockMvc.perform(
      post("/tags/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tagJson)
    )
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.id").value("1"))
      .andExpect(jsonPath("$.name").value("Java"))
      .andExpect(jsonPath("$.snippetId").value("snippet1"))
  }
}
