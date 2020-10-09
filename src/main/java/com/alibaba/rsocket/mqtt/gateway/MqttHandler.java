package com.alibaba.rsocket.mqtt.gateway;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MQTT handler for per channel
 *
 * @author leijuan
 */
@Component("mqttHandler")
@Scope("prototype")
public class MqttHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SubscriptionManager subscriptionManager;
    @Autowired
    private PublishService publishService;

    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private final String uuid = UUID.randomUUID().toString();
    private String clientId;
    private boolean writable = true;
    private static final Logger log = LoggerFactory.getLogger(MqttHandler.class);

    /**
     * entrance for MQTT interaction
     *
     * @param ctx channel handler context
     * @param msg MQTT message
     * @throws Exception exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MqttMessage message = (MqttMessage) msg;
        MqttMessageType mqttMessageType = message.fixedHeader().messageType();
        switch (mqttMessageType) {
            case CONNECT:
                MqttConnAckMessage ackMessage = onConnect((MqttConnectMessage) message, ctx);
                ctx.writeAndFlush(ackMessage);
                break;
            case CONNACK:
                break;
            case PUBLISH:
                MqttPubAckMessage pubAckMessage = onPublish((MqttPublishMessage) message, ctx);
                ctx.writeAndFlush(pubAckMessage);
                break;
            case PUBACK:
                onPubAck((MqttPubAckMessage) message, ctx);
                break;
            case PUBREC:
                break;
            case PUBREL:
                break;
            case PUBCOMP:
                break;
            case SUBSCRIBE:
                MqttSubAckMessage subAckMessage = onSubscribe((MqttSubscribeMessage) message, ctx);
                ctx.writeAndFlush(subAckMessage);
                break;
            case SUBACK:
                break;
            case UNSUBSCRIBE:
                MqttUnsubAckMessage unsubAckMessage = onUnSubscribe((MqttUnsubscribeMessage) message, ctx);
                ctx.writeAndFlush(unsubAckMessage);
                break;
            case UNSUBACK:
                break;
            case PINGREQ:
                MqttMessage pingResp = onPing();
                ctx.writeAndFlush(pingResp);
                break;
            case PINGRESP:
                break;
            case DISCONNECT:
                onDisconnect(ctx);
                break;
            default:
                log.error("Unknown message type:" + mqttMessageType.value());
                break;
        }
    }

    private void onDisconnect(ChannelHandlerContext ctx) {
        ctx.close();
    }

    private void onPubAck(MqttPubAckMessage message, ChannelHandlerContext ctx) {
        System.out.println("pub ack");
    }

    private MqttPubAckMessage onPublish(MqttPublishMessage message, ChannelHandlerContext ctx) {
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        String topicName = variableHeader.topicName();
        publishService.publish(clientId, topicName, message.payload()).subscribe();
        MqttFixedHeader ackFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 2);
        MqttMessageIdVariableHeader messageIdVariableHeader = MqttMessageIdVariableHeader.from(Math.abs(message.variableHeader().packetId()));
        return new MqttPubAckMessage(ackFixedHeader, messageIdVariableHeader);
    }

    private MqttMessage onPing() {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        return new MqttMessage(mqttFixedHeader);
    }

    private MqttUnsubAckMessage onUnSubscribe(MqttUnsubscribeMessage message, ChannelHandlerContext ctx) {
        Flux.fromIterable(message.payload().topics())
                .flatMap(topic -> subscriptionManager.cancel(uuid, clientId, topic))
                .subscribe();
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK,
                false, MqttQoS.AT_MOST_ONCE, false, 2);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(message.variableHeader().messageId());
        return new MqttUnsubAckMessage(fixedHeader, variableHeader);
    }

    private MqttSubAckMessage onSubscribe(MqttSubscribeMessage message, ChannelHandlerContext ctx) {
        Channel ch = ctx.channel();
        MqttMessageIdVariableHeader subMsgVariableHeader = message.variableHeader();
        List<MqttTopicSubscription> subscriptions = message.payload().topicSubscriptions();
        List<Integer> qosList = new LinkedList<>();
        //todo subscribe
        for (MqttTopicSubscription topicSubscription : subscriptions) {
            qosList.add(topicSubscription.qualityOfService().value());
            Subscription subscription = new Subscription(uuid, clientId, topicSubscription.qualityOfService(), topicSubscription.topicName(), atomicInteger);
            subscriptionManager.requestStream(subscription).subscribe(ch::writeAndFlush);
        }
        //sub ack return
        MqttSubAckPayload mqttSubAckPayload = new MqttSubAckPayload(qosList);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                MqttMessageType.SUBACK,
                false,
                MqttQoS.AT_MOST_ONCE,
                false,
                subMsgVariableHeader.messageId());
        return new MqttSubAckMessage(
                mqttFixedHeader,
                subMsgVariableHeader,
                mqttSubAckPayload);
    }

    private MqttConnAckMessage onConnect(MqttConnectMessage mqttMessage, ChannelHandlerContext channelHandlerContext) {
        MqttConnectVariableHeader variableHeader = mqttMessage.variableHeader();
        this.clientId = mqttMessage.payload().clientIdentifier();
        //heart beat
        int keepAlive = variableHeader.keepAliveTimeSeconds();
        channelHandlerContext.pipeline().addBefore(
                "MqttServerHandler",
                "MqttIdleHandler",
                new IdleStateHandler(keepAlive, 0, 0));
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.FAILURE, false, 0x02);
        MqttConnAckVariableHeader connAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        return new MqttConnAckMessage(fixedHeader, connAckVariableHeader);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        writable = true;
        //channel active
        log.info("channelActive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        writable = false;
        //todo clean resources after connection closed
        log.info("channelUnregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        writable = false;
        //channel in active, don't send message
        log.info("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.close();
    }

}
