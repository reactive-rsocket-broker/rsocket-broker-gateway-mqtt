package com.alibaba.rsocket.mqtt.gateway;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Subscription manager
 *
 * @author leijuan
 */
public interface SubscriptionManager {

    Flux<MqttPublishMessage> requestStream(Subscription subscription);

    Mono<Void> cancel(String uuid, String clientId, String topicName);
}
