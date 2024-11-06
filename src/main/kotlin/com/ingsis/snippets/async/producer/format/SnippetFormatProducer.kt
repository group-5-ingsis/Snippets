package com.ingsis.snippets.async.producer.format

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class SnippetFormatProducer @Autowired constructor(
  @Value("\${stream.key}") streamKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamKey, redis) {

  suspend fun publishEvent(snippet: SnippetFormatRequest) {
    val snippetAsJson = JsonUtil.serializeToJson(snippet)
    emit(snippetAsJson).awaitSingle()
  }
}
