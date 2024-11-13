package com.ingsis.snippets.async.producer.test

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class SnippetTestProducer @Autowired constructor(
  @Value("\${stream.format}") streamKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

  suspend fun publishTestRequestEvent(snippet: SnippetTestRequest) {
    val snippetAsJson = JsonUtil.serializeTestToJson(snippet)
    emit(snippetAsJson).awaitSingle()
  }

  suspend fun publishCreateTestEvent(snippet: SnippetCreateTestRequest) {
    val snippetAsJson = JsonUtil.serializeCreateTestToJson(snippet)
    emit(snippetAsJson).awaitSingle()
  }
}