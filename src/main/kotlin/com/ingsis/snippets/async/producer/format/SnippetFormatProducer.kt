package com.ingsis.snippets.async.producer.format

import com.ingsis.snippets.async.JsonUtil
import com.ingsis.snippets.rules.FormatRequest
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class SnippetFormatProducer @Autowired constructor(
  @Value("\${stream.format}") streamRequestKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamRequestKey, redis) {

  suspend fun publishEvent(snippet: FormatRequest) {
    val requestJson = JsonUtil.serializeToJson(snippet)
    emit(requestJson).awaitSingle()
  }
}
