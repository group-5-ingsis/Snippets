package com.ingsis.snippets.snippet

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

@WebMvcTest(SnippetController::class)
class SnippetControllerTests {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var snippetService: SnippetService

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  @Test
  fun `createSnippet should return created snippet`() {
    val snippet = Snippet().apply {
      id = "12345"
      title = "Test Snippet"
      content = "This is a test snippet."
      language = "Kotlin"
    }

    `when`(snippetService.createSnippet(snippet)).thenReturn(snippet)

    val snippetJson = objectMapper.writeValueAsString(snippet)

    mockMvc.perform(
      MockMvcRequestBuilders.post("/snippets/create")
        .contentType(MediaType.APPLICATION_JSON)
        .content(snippetJson)
    )
      .andExpect(MockMvcResultMatchers.status().isCreated)
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Snippet"))

    verify(snippetService).createSnippet(snippet)
  }

  @Test
  fun `getSnippet should return snippet when found`() {
    val snippetId = "12345"
    val snippet = Snippet().apply {
      id = snippetId
      title = "Test Snippet"
      content = "This is a test snippet."
    }

    `when`(snippetService.getSnippet(snippetId)).thenReturn(snippet)

    mockMvc.perform(MockMvcRequestBuilders.get("/snippets/$snippetId"))
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(snippetId))

    verify(snippetService).getSnippet(snippetId)
  }

  @Test
  fun `getSnippet should return 404 when snippet not found`() {
    val snippetId = "12345"
    `when`(snippetService.getSnippet(snippetId)).thenReturn(null)

    mockMvc.perform(MockMvcRequestBuilders.get("/snippets/$snippetId"))
      .andExpect(MockMvcResultMatchers.status().isNotFound)

    verify(snippetService).getSnippet(snippetId)
  }

  @Test
  fun `updateSnippet should return updated snippet when found`() {
    val snippetId = "12345"

    val updatedSnippet = Snippet().apply {
      title = "New Title"
      content = "New Content"
      language = "Kotlin"
    }

    `when`(snippetService.updateSnippet(snippetId, updatedSnippet)).thenReturn(updatedSnippet)

    val snippetJson = objectMapper.writeValueAsString(updatedSnippet)

    mockMvc.perform(
      MockMvcRequestBuilders.put("/snippets/update/$snippetId")
        .contentType(MediaType.APPLICATION_JSON)
        .content(snippetJson)
    )
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("New Title"))

    verify(snippetService).updateSnippet(snippetId, updatedSnippet)
  }

  @Test
  fun `updateSnippet should return 404 when snippet not found`() {
    val snippetId = "12345"
    val updatedSnippet = Snippet()

    `when`(snippetService.updateSnippet(snippetId, updatedSnippet)).thenReturn(null)

    val snippetJson = objectMapper.writeValueAsString(updatedSnippet)

    mockMvc.perform(
      MockMvcRequestBuilders.put("/snippets/update/$snippetId")
        .contentType(MediaType.APPLICATION_JSON)
        .content(snippetJson)
    )
      .andExpect(MockMvcResultMatchers.status().isNotFound)

    verify(snippetService).updateSnippet(snippetId, updatedSnippet)
  }

  @Test
  fun `deleteSnippet should return 200 when snippet exists`() {
    val snippetId = "12345"
    `when`(snippetService.deleteSnippet(snippetId)).thenReturn(true)

    mockMvc.perform(MockMvcRequestBuilders.delete("/snippets/delete/$snippetId"))
      .andExpect(MockMvcResultMatchers.status().isOk)

    verify(snippetService).deleteSnippet(snippetId)
  }

  @Test
  fun `deleteSnippet should return 404 when snippet does not exist`() {
    val snippetId = "12345"
    `when`(snippetService.deleteSnippet(snippetId)).thenReturn(false)

    mockMvc.perform(MockMvcRequestBuilders.delete("/snippets/delete/$snippetId"))
      .andExpect(MockMvcResultMatchers.status().isNotFound)

    verify(snippetService).deleteSnippet(snippetId)
  }
}
