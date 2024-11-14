package com.ingsis.snippets.async.producer.lint

import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.CompletableDeferred
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component

@Component
class LintResultConsumer @Autowired constructor(
  redis: ReactiveRedisTemplate<String, String>,
  @Value("\${stream.lint-response}") streamResponseKey: String,
  @Value("\${groups.product}") groupId: String
) : RedisStreamConsumer<String>(streamResponseKey, groupId, redis) {

  private val formatResponses = mutableMapOf<String, CompletableDeferred<String>>()

  override fun onMessage(record: ObjectRecord<String, String>) {
    val streamValue = record.value
    val response = JsonUtil.deserializeLintResponse(streamValue)

    formatResponses[response.requestId]?.complete(response.status)
    formatResponses.remove(response.requestId)
  }

  fun getFormatResponse(requestId: String): CompletableDeferred<String> {
    return formatResponses.computeIfAbsent(requestId) { CompletableDeferred() }
  }

  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
    return StreamReceiver.StreamReceiverOptions.builder()
      .targetType(String::class.java)
      .build()
  }
}
