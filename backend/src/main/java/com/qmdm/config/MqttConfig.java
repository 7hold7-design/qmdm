package com.qmdm.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MqttConfig {
    
    @Value("${qmdm.mqtt.broker-url}")
    private String brokerUrl;
    
    @Value("${qmdm.mqtt.client-id}")
    private String clientId;
    
    @Value("${qmdm.mqtt.username}")
    private String username;
    
    @Value("${qmdm.mqtt.password}")
    private String password;
    
    @Value("${qmdm.mqtt.topic-prefix}")
    private String topicPrefix;
    
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);
        return factory;
    }
    
    @Bean
    public MqttPahoMessageHandler mqttOutbound(MessageChannel mqttOutboundChannel) {
        MqttPahoMessageHandler messageHandler = 
            new MqttPahoMessageHandler(clientId + "-out", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(topicPrefix + "commands");
        messageHandler.setDefaultQos(1);
        messageHandler.setDefaultRetained(false);
        return messageHandler;
    }
    
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(MessageChannel mqttInboundChannel) {
        MqttPahoMessageDrivenChannelAdapter adapter = 
            new MqttPahoMessageDrivenChannelAdapter(
                clientId + "-in", 
                mqttClientFactory(), 
                topicPrefix + "events/#",
                topicPrefix + "status/#"
            );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel);
        return adapter;
    }
}
