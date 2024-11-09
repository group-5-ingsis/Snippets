package com.ingsis.snippets.snippet

import com.ingsis.snippets.asset.Asset
import com.ingsis.snippets.asset.AssetService
import com.ingsis.snippets.async.JsonUtil
import com.ingsis.snippets.rules.Rule
import com.ingsis.snippets.rules.RuleManager
import org.springframework.stereotype.Service

@Service
class SnippetService(
  private val snippetRepository: SnippetRepository,
  private val assetService: AssetService
) {

  fun createSnippet(userId: String, snippetDto: SnippetDto): Snippet {
    val snippet = Snippet(snippetDto)

    snippet.author = userId

    val savedSnippet = snippetRepository.save(snippet)

    val asset = Asset(
      container = userId,
      key = savedSnippet.id,
      content = snippetDto.content
    )

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  fun getSnippet(id: String): Snippet {
    return snippetRepository.findById(id).orElse(null)
  }

  fun getFormattingRules(userId: String): List<Rule> {
    val rulesJson = assetService.getAssetContent(userId, "FormattingRules")

    return if (rulesJson == "No Content") {
      val defaultFormattingRules = RuleManager.getDefaultFormattingRules()

      val asset = Asset(
        container = userId,
        key = "FormattingRules",
        content = JsonUtil.serializeFormattingRules(defaultFormattingRules)
      )

      assetService.createOrUpdateAsset(asset)

      RuleManager.convertToRuleList(defaultFormattingRules)
    } else {
      val existingFormattingRules = JsonUtil.deserializeFormattingRules(rulesJson)

      RuleManager.convertToRuleList(existingFormattingRules)
    }
  }

  fun getSnippetContent(id: String): String {
    val snippet = getSnippet(id)
    val container = snippet.author
    val key = snippet.id

    return assetService.getAssetContent(container, key)
  }

  fun updateSnippet(id: String, newSnippet: SnippetDto): Snippet {
    val existingSnippet = getSnippet(id)

    val updatedSnippet = updateFields(existingSnippet, newSnippet)

    val savedSnippet = snippetRepository.save(updatedSnippet)

    val asset = Asset(
      container = savedSnippet.author,
      key = savedSnippet.id,
      content = newSnippet.content
    )

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  private fun updateFields(existingSnippet: Snippet, updatedSnippet: SnippetDto): Snippet {
    return Snippet(
      id = existingSnippet.id,
      author = existingSnippet.author,
      language = updatedSnippet.language,
      extension = updatedSnippet.extension,
      name = updatedSnippet.name,
      compliant = "unknown"
    )
  }

  fun deleteSnippet(id: String) {
    val snippet = getSnippet(id)
    val container = snippet.author
    val key = snippet.id
    assetService.deleteAsset(container, key)
    snippetRepository.deleteById(id)
  }
}
