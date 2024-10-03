package com.ingsis.snippets.tag

import org.springframework.stereotype.Service

@Service
class TagService(private val tagRepository: TagRepository) {

  fun createTag(tag: Tag): Tag {
    return tagRepository.save(tag)
  }

  fun getAllTags(): List<Tag> {
    return tagRepository.findAll()
  }

  fun getTagById(id: String): Tag? {
    return tagRepository.findById(id).orElse(null)
  }

  fun updateTag(id: String, updatedTag: Tag): Tag? {
    val existingTag = tagRepository.findById(id).orElse(null)
    return if (existingTag != null) {
      existingTag.name = updatedTag.name
      existingTag.snippetId = updatedTag.snippetId
      tagRepository.save(existingTag)
    } else {
      null
    }
  }

  fun deleteTag(id: String): Boolean {
    return if (tagRepository.existsById(id)) {
      tagRepository.deleteById(id)
      true
    } else {
      false
    }
  }
}
