package com.example.snippets.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")

class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private var id: Long? = null,

  private var username: String? = null,

  private var email: String? = null,

  private var password: String? = null,

  private var registrationDate: LocalDateTime = LocalDateTime.now(),

  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
  private var snippets: List<Snippet> = emptyList()
)
