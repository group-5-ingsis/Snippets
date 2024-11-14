package com.ingsis.snippets.async.producer.test

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
class TestedSnippetConsumer @Autowired constructor(
  redis: ReactiveRedisTemplate<String, String>,
  @Value("\${stream.test-result}") streamResponseKey: String,
  @Value("\${groups.test-res}") groupId: String
) : RedisStreamConsumer<String>(streamResponseKey, groupId, redis) {

  private val testResponses = mutableMapOf<String, CompletableDeferred<Boolean>>()

  override fun onMessage(record: ObjectRecord<String, String>) {
    val streamValue = record.value
    val response = JsonUtil.deserializeTestResponse(streamValue)

    testResponses[response.requestId]?.complete(response.passed)
    testResponses.remove(response.requestId)
  }

  fun getFormatResponse(requestId: String): CompletableDeferred<Boolean> {
    return testResponses.computeIfAbsent(requestId) { CompletableDeferred() }
  }

  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
    return StreamReceiver.StreamReceiverOptions.builder()
      .targetType(String::class.java)
      .build()
  }
}
