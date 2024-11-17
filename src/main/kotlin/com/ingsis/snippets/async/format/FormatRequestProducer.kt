package com.ingsis.snippets.async.format

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class FormatRequestProducer @Autowired constructor(
  @Value("\${stream.format}") streamRequestKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamRequestKey, redis) {

  private val logger = LoggerFactory.getLogger(FormatRequestProducer::class.java)

  suspend fun publishEvent(snippet: FormatRequest) {
    val requestJson = JsonUtil.serializeToJson(snippet)
    emit(requestJson).awaitSingle()
    logger.info("Sent request to format snippet for user: ${snippet.author}")
  }
}
