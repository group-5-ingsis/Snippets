package com.ingsis.snippets.async.format

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

@Component
class FormattedSnippetConsumer @Autowired constructor(
  redis: ReactiveRedisTemplate<String, String>,
  @Value("\${stream.format-response}") streamResponseKey: String,
  @Value("\${groups.product}") groupId: String
) : RedisStreamConsumer<String>(streamResponseKey, groupId, redis) {

  private val formatResponses = mutableMapOf<String, CompletableDeferred<String>>()

  private val logger = LoggerFactory.getLogger(FormattedSnippetConsumer::class.java)

  override fun onMessage(record: ObjectRecord<String, String>) {
    val streamValue = record.value
    logger.info("Received message from stream: $streamValue")

    try {
      val response = JsonUtil.deserializeFormatResponse(streamValue)
      logger.info("Deserialized response with requestId: ${response.requestId}")

      val completableDeferred = formatResponses[response.requestId]
      if (completableDeferred != null) {
        completableDeferred.complete(response.content)
        logger.info("Completed CompletableDeferred for requestId: ${response.requestId}")
      } else {
        logger.warn("No CompletableDeferred found for requestId: ${response.requestId}")
      }

      formatResponses.remove(response.requestId)
      logger.info("Removed requestId: ${response.requestId} from formatResponses map")
    } catch (e: Exception) {
      logger.error("Error processing message: $streamValue", e)
    }
  }

  fun getFormatResponse(requestId: String): CompletableDeferred<String> {
    logger.info("Retrieving CompletableDeferred for requestId: $requestId")
    return formatResponses.computeIfAbsent(requestId) {
      logger.info("Created new CompletableDeferred for requestId: $requestId")
      CompletableDeferred()
    }
  }

  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
    return StreamReceiver.StreamReceiverOptions.builder()
      .targetType(String::class.java)
      .build()
  }
}
