// package com.ingsis.snippets.snippet.consumer
//
// import org.austral.ingsis.redis.RedisStreamConsumer
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.beans.factory.annotation.Value
// import org.springframework.data.redis.connection.stream.ObjectRecord
// import org.springframework.data.redis.core.ReactiveRedisTemplate
// import org.springframework.data.redis.stream.StreamReceiver
// import org.springframework.stereotype.Component
// import kotlin.time.Duration
//
//
// @Component
// class OperationConsumer @Autowired constructor(
//  redis: ReactiveRedisTemplate<String, String>,
//  @Value("\${stream.key}") streamKey: String,
//  @Value("\${groups.product}") groupId: String
// ) : RedisStreamConsumer<OperationResult>(streamKey, groupId, redis){
//
//  override fun onMessage(record: ObjectRecord<String, OperationResult>): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, ProductCreated>> {
//    return StreamReceiver.StreamReceiverOptions.builder()
//      .targetType(OperationResult::class.java)
//      .build();
//  }
//
//  override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, OperationResult>> {
//    TODO("Not yet implemented")
//  }
//
// }
