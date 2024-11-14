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
  private val assetService: AssetService,
  private val permissionService: PermissionService
) {

  fun createSnippet(userId: String, username: String, snippetDto: SnippetDto): Snippet {
    val snippet = Snippet(snippetDto)

    snippet.author = username

    val savedSnippet = snippetRepository.save(snippet)
    val createdSnippetId = savedSnippet.id

    val asset = Asset(
      container = username,
      key = createdSnippetId,
      content = snippetDto.content
    )

    val userData = UserData(userId, username)

    permissionService.updatePermissions(userData, createdSnippetId, "read")
    permissionService.updatePermissions(userData, createdSnippetId, "write")

    assetService.createOrUpdateAsset(asset)

    return savedSnippet
  }

  fun getSnippetById(id: String): Snippet {
    return snippetRepository.findById(id).orElse(null)
  }

  fun getSnippetContent(id: String): SnippetWithContent {
    val snippet = getSnippetById(id)
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    val snippetWithContent = SnippetWithContent(snippet, content)
    return snippetWithContent
  }

  fun getSnippetsByName(name: String): List<Snippet> {
    val mySnippetsIds = permissionService.getMySnippetsIds()

    return if (name.isBlank()) {
      snippetRepository.findAll().filter { it.id in mySnippetsIds }
    } else {
      snippetRepository.findByName(name).filter { it.id in mySnippetsIds }
    }
  }

  fun getSnippets(): List<Snippet> {
    return snippetRepository.findAll()
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

  fun getLintingRules(userId: String): List<Rule> {
    val rulesJson = assetService.getAssetContent(userId, "LintingRules")

    return if (rulesJson == "No Content") {
      val defaultLintingRules = RuleManager.getDefaultLintingRules()

      val asset = Asset(
        container = userId,
        key = "LintingRules",
        content = JsonUtil.serializeLintingRules(defaultLintingRules)
      )

      assetService.createOrUpdateAsset(asset)

      RuleManager.convertToRuleList(defaultLintingRules)
    } else {
      val existingFormattingRules = JsonUtil.deserializeFormattingRules(rulesJson)

      RuleManager.convertToRuleList(existingFormattingRules)
    }
  }

  fun updateSnippet(id: String, newContent: String): SnippetWithContent {
    val existingSnippet = getSnippetById(id)

    val asset = Asset(
      container = existingSnippet.author,
      key = existingSnippet.id,
      content = newContent
    )

    assetService.createOrUpdateAsset(asset)

    return SnippetWithContent(existingSnippet, newContent)
  }

  fun deleteSnippet(id: String) {
    val snippet = getSnippetById(id)
    val container = snippet.author
    val key = snippet.id
    assetService.deleteAsset(container, key)
    snippetRepository.deleteById(id)
  }

  fun shareSnippet(userId: String, snippetId: String, userToShare: String): SnippetWithContent {
    val snippet = getSnippetById(snippetId)
    val writableSnippets = permissionService.getSnippets(userId, "write")
    if (snippet.id in writableSnippets) {
      permissionService.shareSnippet(userToShare, snippetId)
    }
    val content = assetService.getAssetContent(snippet.author, snippet.id)
    return SnippetWithContent(snippet, content)
  }
}
