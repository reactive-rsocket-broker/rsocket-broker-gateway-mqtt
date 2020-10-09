package com.alibaba.rsocket.mqtt.gateway.impl;

import com.alibaba.rsocket.mqtt.gateway.PublishService;
import com.alibaba.rsocket.mqtt.gateway.TopicStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * publish service implementation
 *
 * @author leijuan
 */
@Component
public class PublishServiceImpl implements PublishService {
    @Autowired
    private TopicStore topicStore;

    @Override
    public Mono<Void> publish(String clientId, String topic, ByteBuf payload) {
        topicStore.save(topic, ByteBufUtil.getBytes(payload));
        return Mono.empty();
    }
}
