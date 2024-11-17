// package com.ingsis.snippets.snippet
//
// import com.ingsis.snippets.asset.Asset
// import com.ingsis.snippets.asset.AssetService
// import org.junit.jupiter.api.Assertions.*
// import org.junit.jupiter.api.BeforeEach
// import org.junit.jupiter.api.Test
// import org.mockito.InjectMocks
// import org.mockito.Mock
// import org.mockito.Mockito.*
// import org.mockito.MockitoAnnotations
// import org.springframework.web.client.RestTemplate
//
// class SnippetServiceTests {
//
//  @InjectMocks
//  private lateinit var snippetService: com.ingsis.snippets.snippet.SnippetService
//
//  @Mock
//  private lateinit var snippetRepository: SnippetRepository
//
//  @Mock
//  private lateinit var assetService: AssetService
//
//  @Mock
//  private lateinit var restTemplate: RestTemplate
//
//  @BeforeEach
//  fun setUp() {
//    MockitoAnnotations.openMocks(this)
//  }
//
//  @Test
//  fun `should return Snippet created successfully when snippet is created`() {
//    val asset = Asset(container = "PrintScript", key = "HelloWorld.ps", content = "HelloWorld!")
//
//    `when`(assetService.createOrUpdateAsset(asset))
//      .thenReturn("Snippet created successfully.")
//
//    val snippetDto = SnippetDto(
//      author = "1",
//      name = "HelloWorld.ps",
//      description = "my cool snippet",
//      version = "1.1",
//      language = "PrintScript",
//      content = "println(2 + 2);"
//    )
//
//    val snippet = Snippet(snippetDto)
//
//    `when`(snippetRepository.save(any(Snippet::class.java))).thenReturn(snippet)
//
//    val result = snippetService.createSnippet(snippetDto)
//
//    assertEquals(snippet, result)
//  }
//
//  @Test
//  fun `createOrUpdateSnippet should return Snippet updated successfully when snippet is updated`() {
//    val asset = Asset(container = "PrintScript", key = "HelloWorld.ps", content = "HelloWorld!")
//
//    `when`(assetService.createOrUpdateAsset(asset))
//      .thenReturn("Snippet updated successfully.")
//
//    val snippetDto = SnippetDto(
//      author = "1",
//      name = "HelloWorld.ps",
//      description = "my cool snippet",
//      version = "1.1",
//      language = "PrintScript",
//      content = "println(2 + 2);"
//    )
//
//    val snippet = Snippet(snippetDto)
//    `when`(snippetRepository.save(any(Snippet::class.java))).thenReturn(snippet)
//
//    val result = snippetService.createSnippet(snippetDto)
//
//    assertEquals(snippet, result)
//  }
// }
