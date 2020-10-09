package com.alibaba.rsocket.mqtt.gateway.impl;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * SimpleTopic test
 *
 * @author leijuan
 */
public class SimpleTopicStoreTest {
    private static SimpleTopicStore topicStore = new SimpleTopicStore();

    @Test
    public void testPubSub() throws Exception {
        String topicName = "test/topic";
        topicStore.receive(topicName).subscribe(bytes -> {
            System.out.println("Received:" + new String(bytes));
        });
        topicStore.save(topicName, "Hello".getBytes(StandardCharsets.UTF_8)).subscribe();
        Thread.sleep(1000);
    }
}
