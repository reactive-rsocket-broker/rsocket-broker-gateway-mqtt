package com.alibaba.rsocket.mqtt.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RSocket broker gateway MQTT Server
 *
 * @author leijuan
 */
@SpringBootApplication
public class RSocketBrokerGatewayMqttServer {

    public static void main(String[] args) {
        SpringApplication.run(RSocketBrokerGatewayMqttServer.class, args);
    }

}

