package com.alibaba.rsocket.mqtt.gateway.impl;

import com.alibaba.rsocket.mqtt.gateway.Subscription;
import com.alibaba.rsocket.mqtt.gateway.SubscriptionManager;
import com.alibaba.rsocket.mqtt.gateway.TopicStore;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Subscription manager implementation
 *
 * @author leijuan
 */
@Component
public class SubscriptionManagerImpl implements SubscriptionManager {
    @Autowired
    private TopicStore topicStore;

    @Override
    public Flux<MqttPublishMessage> requestStream(Subscription subscription) {
        return topicStore.receive(subscription.getTopicName()).map(bytes -> createPublishMessage(subscription, bytes));
    }

    @Override
    public Mono<Void> cancel(String uuid, String clientId, String topicName) {
        return Mono.empty();
    }

    public MqttPublishMessage createPublishMessage(Subscription subscription, byte[] bytes) {
        MqttFixedHeader header = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(subscription.getTopicName(), subscription.getNextMessageId());
        return (MqttPublishMessage) MqttMessageFactory.newMessage(header, variableHeader, Unpooled.wrappedBuffer(bytes));
    }
}
