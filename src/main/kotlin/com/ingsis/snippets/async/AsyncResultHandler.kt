package com.ingsis.snippets.async

import kotlinx.coroutines.CompletableDeferred
import org.slf4j.Logger

object AsyncResultHandler {
  fun <T> processMessage(
    logger: Logger,
    responses: MutableMap<String, CompletableDeferred<T>>,
    streamValue: String,
    deserialize: (String) -> AsyncResponse<T>
  ) {
    try {
      val response = deserialize(streamValue)
      logger.info("Deserialized response with requestId: ${response.requestId}")

      val asyncResult = responses[response.requestId]
      if (asyncResult != null) {
        asyncResult.complete(response.content)
        logger.info("Completed AsyncResult for requestId: ${response.requestId}")
      } else {
        logger.warn("No AsyncResult found for requestId: ${response.requestId}")
      }

      responses.remove(response.requestId)
      logger.info("Removed AsyncResult for requestId: ${response.requestId} from responses map")
    } catch (e: Exception) {
      logger.error("Error processing message: $streamValue", e)
    }
  }

  fun <T> getAsyncResult(
    logger: Logger,
    responses: MutableMap<String, CompletableDeferred<T>>,
    requestId: String
  ): CompletableDeferred<T> {
    logger.info("Retrieving AsyncResult for requestId: $requestId")
    return responses.computeIfAbsent(requestId) {
      logger.info("Created new AsyncResult for requestId: $requestId")
      CompletableDeferred()
    }
  }

  data class AsyncResponse<T>(val requestId: String, val content: T)
}
