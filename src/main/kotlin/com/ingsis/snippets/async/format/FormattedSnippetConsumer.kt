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

    AsyncResultHandler.processMessage(
      logger,
      formatResponses,
      streamValue
    ) { stream ->
      val response = JsonUtil.deserializeFormatResponse(stream)
      AsyncResultHandler.AsyncResponse(response.requestId, response.content)
    }
  }

  fun getFormatResponse(requestId: String): CompletableDeferred<String> {
    return AsyncResultHandler.getAsyncResult(logger, formatResponses, requestId)
  }

  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
    return StreamReceiver.StreamReceiverOptions.builder()
      .targetType(String::class.java)
      .build()
  }
}
