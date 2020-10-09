package com.alibaba.rsocket.mqtt.gateway;

import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * subscription from client
 *
 * @author leijuan
 */
public class Subscription {
    private String uuid;
    private String clientId;
    private MqttQoS mqttQoS;
    private String topicName;
    private AtomicInteger atomicInteger;

    public Subscription() {
    }

    public Subscription(String uuid, String clientId, MqttQoS mqttQoS, String topicName, AtomicInteger atomicInteger) {
        this.uuid = uuid;
        this.clientId = clientId;
        this.mqttQoS = mqttQoS;
        this.topicName = topicName;
        this.atomicInteger = atomicInteger;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MqttQoS getMqttQoS() {
        return mqttQoS;
    }

    public void setMqttQoS(MqttQoS mqttQoS) {
        this.mqttQoS = mqttQoS;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getNextMessageId() {
        return atomicInteger.getAndIncrement();
    }
}
