package com.ingsis.snippets.async.lint

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component

@Component
class LintRequestProducer @Autowired constructor(
  @Value("\${stream.lint-request}") streamRequestKey: String,
  redis: ReactiveRedisTemplate<String, String>
) : RedisStreamProducer(streamRequestKey, redis) {

  private val logger = LoggerFactory.getLogger(LintRequestProducer::class.java)

  suspend fun publishEvent(snippet: LintRequest) {
    val requestJson = JsonUtil.serializeToJson(snippet)
    emit(requestJson).awaitSingle()
    logger.info("Sent request to lint snippet for user: ${snippet.author}")
  }
}
