package com.ingsis.snippets.async.producer.test

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Component

@SpringBootApplication
@EnableAsync
open class SnippetTestProducer @Autowired constructor(
  @Value("\${stream.test}") streamKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

  suspend fun publishTestRequestEvent(snippet: SnippetTestRequest) {
    val snippetAsJson = JsonUtil.serializeTestToJson(snippet)
    emit(snippetAsJson).awaitSingle()
  }
}
