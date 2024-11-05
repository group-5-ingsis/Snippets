package com.ingsis.snippets.snippet.async.producer

import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class SnippetProducer @Autowired constructor(
  @Value("\${stream.key}") streamKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

  suspend fun publishEvent(snippetId: String, name: String, operation: String) {
    val product = SnippetOperation(snippetId, name, operation)
    emit(product).awaitSingle()
  }
}
