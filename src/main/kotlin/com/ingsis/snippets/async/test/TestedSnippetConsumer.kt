package com.ingsis.snippets.async.test

import com.ingsis.snippets.async.AsyncResultHandler
import com.ingsis.snippets.async.JsonUtil
import kotlinx.coroutines.CompletableDeferred
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TestedSnippetConsumer @Autowired constructor(
  redis: ReactiveRedisTemplate<String, String>,
  @Value("\${stream.test-result}") streamResponseKey: String,
  @Value("\${groups.test-res}") groupId: String
) : RedisStreamConsumer<String>(streamResponseKey, groupId, redis) {

  private val testResponses = mutableMapOf<String, CompletableDeferred<Boolean>>()
  private val logger = LoggerFactory.getLogger(TestedSnippetConsumer::class.java)

  override fun onMessage(record: ObjectRecord<String, String>) {
    val streamValue = record.value
    logger.info("Received message from stream: $streamValue")

    try {
      AsyncResultHandler.processMessage(
        logger,
        testResponses,
        streamValue
      ) { stream ->
        logger.debug("Processing stream value: $stream")
        val response = JsonUtil.deserializeTestResponse(stream)
        logger.info("Deserialized response: requestId=${response.requestId}, content=${response.passed}")
        AsyncResultHandler.AsyncResponse(response.requestId, response.passed)
      }
    } catch (e: Exception) {
      logger.error("Error processing message from stream: $streamValue", e)
    }
  }

  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
    return StreamReceiver.StreamReceiverOptions.builder()
      .targetType(String::class.java)
      .pollTimeout(Duration.ofSeconds(5))
      .build()
  }

  fun registerTestResponse(requestId: String): CompletableDeferred<Boolean> {
    val deferred = CompletableDeferred<Boolean>()
    testResponses[requestId] = deferred
    return deferred
  }
}
