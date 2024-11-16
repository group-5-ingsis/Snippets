package com.ingsis.snippets.user

import com.fasterxml.jackson.annotation.JsonProperty

data class UserDto(
  @JsonProperty("id") val id: String,
  @JsonProperty("name") val name: String
)
