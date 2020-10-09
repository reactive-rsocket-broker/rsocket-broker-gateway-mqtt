package com.alibaba.rsocket.mqtt.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT auto configuration
 *
 * @author leijuan
 */
@Configuration
public class MqttAutoConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * start MQTT server
     *
     * @return netty loop group
     * @throws Exception exception
     */
    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup mqttEventLoopGroup() throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new MqttDecoder());
                        socketChannel.pipeline().addLast(MqttEncoder.INSTANCE);
                        socketChannel.pipeline().addLast("MqttServerHandler", (ChannelHandler) applicationContext.getBean("mqttHandler"));
                    }
                });
        serverBootstrap.bind("0.0.0.0", 1883).sync();
        return eventLoopGroup;
    }
}
