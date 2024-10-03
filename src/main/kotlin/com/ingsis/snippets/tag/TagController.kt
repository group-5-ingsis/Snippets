package com.ingsis.snippets.tag

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tags")
class TagController(private val tagService: TagService) {

  @PostMapping("/create")
  fun createTag(@RequestBody tag: Tag): ResponseEntity<Tag> {
    val createdTag = tagService.createTag(tag)
    return ResponseEntity.ok(createdTag)
  }

  @GetMapping
  fun getAllTags(): ResponseEntity<List<Tag>> {
    return ResponseEntity.ok(tagService.getAllTags())
  }

  @GetMapping("/get/{id}")
  fun getTagById(@PathVariable id: String): ResponseEntity<Tag> {
    val tag = tagService.getTagById(id)
    return if (tag != null) {
      ResponseEntity.ok(tag)
    } else {
      ResponseEntity.notFound().build()
    }
  }

  @PutMapping("/update/{id}")
  fun updateTag(@PathVariable id: String, @RequestBody updatedTag: Tag): ResponseEntity<Tag> {
    val tag = tagService.updateTag(id, updatedTag)
    return if (tag != null) {
      ResponseEntity.ok(tag)
    } else {
      ResponseEntity.notFound().build()
    }
  }

  @DeleteMapping("/delete/{id}")
  fun deleteTag(@PathVariable id: String): ResponseEntity<Void> {
    val deletedTag = tagService.deleteTag(id)
    return if (deletedTag) {
      ResponseEntity.ok().build()
    } else {
      ResponseEntity.notFound().build()
    }
  }
}
