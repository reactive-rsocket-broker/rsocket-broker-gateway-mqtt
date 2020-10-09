package com.alibaba.rsocket.mqtt.gateway.impl;

import com.alibaba.rsocket.mqtt.gateway.TopicStore;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.extra.processor.TopicProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple topic store
 *
 * @author leijuan
 */
@Repository
public class SimpleTopicStore implements TopicStore {
    private Map<String, TopicProcessor<byte[]>> topics = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> save(String topicName, byte[] bytes) {
        if (!topics.containsKey(topicName)) {
            initTopic(topicName);
        }
        topics.get(topicName).onNext(bytes);
        return Mono.empty();
    }

    @Override
    public Flux<byte[]> receive(String topicName) {
        if (!topics.containsKey(topicName)) {
            initTopic(topicName);
            //todo request/stream remote RSocket
        }
        return topics.get(topicName);
    }

    private void initTopic(String topicName) {
        topics.putIfAbsent(topicName, TopicProcessor.create());
    }
}
