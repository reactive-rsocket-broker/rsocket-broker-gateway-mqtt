package com.alibaba.rsocket.mqtt.gateway;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Topic store
 *
 * @author leijuan
 */
public interface TopicStore {

    Mono<Void> save(String topicName, byte[] bytes);

    Flux<byte[]> receive(String topicName);
}
