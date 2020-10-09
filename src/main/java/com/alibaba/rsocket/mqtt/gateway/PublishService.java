package com.alibaba.rsocket.mqtt.gateway;

import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Mono;

/**
 * Publish service
 *
 * @author leijuan
 */
public interface PublishService {

     Mono<Void> publish(String clientId, String topic, ByteBuf payload);
}
