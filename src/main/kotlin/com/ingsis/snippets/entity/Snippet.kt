package com.ingsis.snippets.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "snippets")
class Snippet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private var id: Long? = null

  private var title: String? = null
  private var content: String? = null
  private var language: String? = null
  private var creationDate: LocalDateTime? = null
  private var modificationDate: LocalDateTime? = null

  @ManyToOne
  @JoinColumn(name = "user_id")
  private var user: User? = null

  @OneToMany(mappedBy = "snippet", cascade = [CascadeType.ALL])
  private var comments: List<Comment>? = null

  @OneToMany(mappedBy = "snippet", cascade = [CascadeType.ALL])
  private var testCases: List<TestCase>? = null

  @ManyToMany
  @JoinTable(
    name = "snippet_tag",
    joinColumns = [JoinColumn(name = "snippet_id")],
    inverseJoinColumns = [JoinColumn(name = "tag_id")]
  )
  private var tags: List<Tag>? = null
}
