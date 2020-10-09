RSocket Broker Gateway MQTT
===========================

RSocket MQTT Gateway, 通过MQTT协议访问RSocket的Reactive服务。

### Why MQTT with RSocket?

* Let IoT easy to talk with Reactive Systems
* Connect everything including backend App, Kafka, Data Streams.

### MQTT 和 RSocket整合方案

* MQTT的订阅对应于RSocket request/stream，将MQTT的Topic和一个request/stream的Flux进行关联
* 反向推送： MQTT Gateway提供一个RSocket接口，负责接收发送给MQTT Gateway的消息，然后再将消息转发给MQTT订阅的客户端

当然也可以借助于其他的MQ系统，如MQTT Gateway负责和Kafka对接，处理来自Kafka的消息然后再转发给MQTT的订阅者。

### MQTT 5 Features

* Properties in the MQTT Header & Reason Codes: possibility to add custom key-value properties in the MQTT header, MQTT packets include Reason Codes. A Reason Code indicates that a pre-defined protocol error occurred.
* CONNACK Return Codes for unsupported features
* Additional MQTT Packet: new AUTH packet
* New Data Type: UTF-8 String pairs
* Bi-directional DISCONNECT packets
* Using passwords without username

Netty 4.1.52 has MQTT5 support for netty-codec-mqtt.

### References

* MQTT: http://mqtt.org/
* Hands-On Internet of Things with MQTT: https://learning.oreilly.com/library/view/hands-on-internet-of/9781789341782
* 初识 MQTT 为什么 MQTT 是最适合物联网的网络协议  https://www.ibm.com/developerworks/cn/iot/iot-mqtt-why-good-for-iot/index.html
* MQTT协议中文: https://mcxiaoke.gitbooks.io/mqtt-cn/content/
* MQTT 5: hhttps://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html
* Eclipse Mosquitto: open source message broker that implements the MQTT protocol versions 3.1 and 3.1.1 https://mosquitto.org/
* Eclipse Paho: open-source client implementations of MQTT and MQTT-SN messaging protocols https://www.eclipse.org/paho/
* HiveMQ: MQTT based messaging platform designed for the fast, efficient and reliable movement of data to and from connected IoT devices https://www.hivemq.com/
* Reactor Netty: https://github.com/reactor/reactor-netty
* Open-source IoT Platform: https://github.com/actorcloud/ActorCloud
