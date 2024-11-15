package com.ingsis.snippets.async.test

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class SnippetCreateTestProducer @Autowired constructor(
  @Value("\${stream.test-create}") streamKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

  suspend fun publishCreateTestEvent(snippet: SnippetCreateTestRequest) {
    println("Publishing on stream $streamKey")
    val snippetAsJson = JsonUtil.serializeCreateTestToJson(snippet)
    emit(snippetAsJson).awaitSingle()
  }
}
