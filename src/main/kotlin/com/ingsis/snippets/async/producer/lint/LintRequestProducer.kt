package com.ingsis.snippets.async.producer.lint

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class LintRequestProducer @Autowired constructor(
  @Value("\${stream.lint}") streamRequestKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamRequestKey, redis) {

  suspend fun publishEvent(snippet: SnippetLintRequest) {
    val requestJson = JsonUtil.serializeToJson(snippet)
    emit(requestJson).awaitSingle()
  }
}
