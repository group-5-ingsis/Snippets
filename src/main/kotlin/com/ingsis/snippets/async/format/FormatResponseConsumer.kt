package com.ingsis.snippets.async.format

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
class FormatResponseConsumer @Autowired constructor(
  redis: ReactiveRedisTemplate<String, String>,
  @Value("\${stream.format-response}") streamResponseKey: String,
  @Value("\${groups.product}") groupId: String
) : RedisStreamConsumer<String>(streamResponseKey, groupId, redis) {

  private val formatResponses = mutableMapOf<String, CompletableDeferred<String>>()
  private val logger = LoggerFactory.getLogger(FormatResponseConsumer::class.java)

  init {
    logger.info("FormatResponseConsumer initialized with stream key: $streamResponseKey and group ID: $groupId")
  }

  override fun onMessage(record: ObjectRecord<String, String>) {
    val streamValue = record.value
    logger.info("Received message from stream: $streamValue")

    try {
      AsyncResultHandler.processMessage(
        logger,
        formatResponses,
        streamValue
      ) { stream ->
        logger.debug("Processing stream value: $stream")
        val response = JsonUtil.deserializeFormatResponse(stream)
        logger.info("Deserialized response: requestId=${response.requestId}, content=${response.content}")
        AsyncResultHandler.AsyncResponse(response.requestId, response.content)
      }
    } catch (e: Exception) {
      logger.error("Error processing message from stream: $streamValue", e)
    }
  }

  fun getFormatResponse(requestId: String): CompletableDeferred<String> {
    logger.info("Fetching format response for requestId: $requestId")
    return AsyncResultHandler.getAsyncResult(logger, formatResponses, requestId).also {
      logger.debug("Returned CompletableDeferred for requestId: $requestId")
    }
  }

  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
    logger.debug("Configuring StreamReceiver options for FormatResponseConsumer")
    return StreamReceiver.StreamReceiverOptions.builder()
      .targetType(String::class.java)
      .pollTimeout(Duration.ofSeconds(1))
      .build()
  }
}
