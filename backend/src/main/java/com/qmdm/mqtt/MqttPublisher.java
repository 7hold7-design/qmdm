package com.qmdm.mqtt;

import com.qmdm.model.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttPublisher {
    
    private final MessageChannel mqttOutboundChannel;
    private final ObjectMapper objectMapper;
    
    public void publishCommand(String deviceId, Command command) {
        try {
            String topic = "qmdm/commands/" + deviceId;
            String payload = objectMapper.writeValueAsString(command);
            
            Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();
            
            mqttOutboundChannel.send(message);
            log.info("Command published to device {}: {}", deviceId, command.getType());
        } catch (Exception e) {
            log.error("Failed to publish command to device {}: {}", deviceId, e.getMessage());
        }
    }
    
    public void publishDeviceStatus(String deviceId, Object status) {
        try {
            String topic = "qmdm/status/" + deviceId;
            String payload = objectMapper.writeValueAsString(status);
            
            Message<String> message = MessageBuilder.withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .setHeader(MqttHeaders.RETAINED, true)
                .build();
            
            mqttOutboundChannel.send(message);
            log.debug("Device status published for: {}", deviceId);
        } catch (Exception e) {
            log.error("Failed to publish device status: {}", e.getMessage());
        }
    }
}
