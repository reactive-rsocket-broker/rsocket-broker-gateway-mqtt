package com.alibaba.rsocket.mqtt.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

/**
 * RSocket Controller
 *
 * @author leijuan
 */
@Controller
public class RSocketController {
    @Autowired
    private TopicStore topicStore;

    @MessageMapping("topic.{name}")
    public Mono<String> send(@DestinationVariable("name") String name, String payload) {
        return topicStore.save(name, payload.getBytes())
                .then(Mono.just("Success"));
    }
}
