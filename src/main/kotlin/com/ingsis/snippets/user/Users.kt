package com.ingsis.snippets.user

import com.fasterxml.jackson.annotation.JsonProperty

data class Auth0User(
  @JsonProperty("user_id") val id: String,
  @JsonProperty("username") val name: String
)

data class UserDto(
  val id: String,
  val name: String
)

fun Auth0User.toUserDto(): UserDto {
  return UserDto(id = this.id, name = this.name)
}

data class UserData(
  val userId: String,
  val username: String
)
