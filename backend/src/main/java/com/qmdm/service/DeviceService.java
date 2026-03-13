package com.qmdm.service;

import com.qmdm.dto.DeviceDTO;
import com.qmdm.dto.CommandDTO;
import com.qmdm.dto.CommandResult;
import com.qmdm.dto.hmdm.DeviceRegistrationRequest;
import com.qmdm.dto.hmdm.DeviceConfigurationResponse;
import com.qmdm.dto.hmdm.CommandRequest;
import com.qmdm.model.Device;
import com.qmdm.model.Command;
import com.qmdm.repository.DeviceRepository;
import com.qmdm.repository.CommandRepository;
import com.qmdm.mqtt.MqttPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final CommandRepository commandRepository;
    private final MqttPublisher mqttPublisher;
    
    public Map<String, Object> registerDevice(DeviceRegistrationRequest request) {
        // Проверяем, существует ли уже устройство
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
            .orElse(new Device());
        
        // Обновляем или создаём устройство
        device.setDeviceId(request.getDeviceId());
        device.setModel(request.getModel());
        device.setManufacturer(request.getManufacturer());
        device.setAndroidVersion(request.getAndroidVersion());
        device.setApiLevel(request.getApiLevel());
        device.setLastSeen(LocalDateTime.now());
        
        if (device.getId() == null) {
            device.setStatus(Device.DeviceStatus.ACTIVE);
            device.setEnrolledAt(LocalDateTime.now());
            device.setEnrollmentToken(UUID.randomUUID().toString());
        }
        
        deviceRepository.save(device);
        
        // Возвращаем ответ в формате hmdm
        return Map.of(
            "status", "success",
            "deviceId", device.getDeviceId(),
            "enrollmentToken", device.getEnrollmentToken(),
            "serverTime", System.currentTimeMillis()
        );
    }
    
    @Cacheable(value = "deviceConfig", key = "#deviceId")
    public DeviceConfigurationResponse getDeviceConfiguration(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));
        
        DeviceConfigurationResponse response = new DeviceConfigurationResponse();
        response.setDeviceId(deviceId);
        response.setConfiguration(loadConfigurationForDevice(device));
        
        return response;
    }
    
    public void processDeviceReport(String deviceId, Map<String, Object> report) {
        deviceRepository.findByDeviceId(deviceId).ifPresent(device -> {
            device.setLastSeen(LocalDateTime.now());
            device.getProperties().putAll((Map) report.get("properties"));
            deviceRepository.save(device);
        });
    }
    
   public List<CommandDTO> getPendingCommands(String deviceId) {
    List<Command> commands = commandRepository.findByDeviceIdAndStatus(
        deviceId, Command.CommandStatus.PENDING);
    
    return commands.stream()
        .map(CommandDTO::fromEntity)
        .collect(Collectors.toList());
}
    
    public void updateCommandStatus(String commandId, Map<String, Object> result) {
        commandRepository.findById(commandId).ifPresent(command -> {
            command.setStatus(Command.CommandStatus.COMPLETED);
            command.setResult(result);
            command.setCompletedAt(LocalDateTime.now());
            commandRepository.save(command);
        });
    }
    
    public void sendCommandToDevice(String deviceId, Command.CommandType type, 
                                    Map<String, Object> parameters) {
        Command command = new Command();
        command.setDeviceId(deviceId);
        command.setType(type);
        command.setParameters(parameters);
        command.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        
        command = commandRepository.save(command);
        
        // Отправляем через MQTT
        mqttPublisher.publishCommand(deviceId, command);
    }
    
    private Map<String, Object> loadConfigurationForDevice(Device device) {
        // Загружаем конфигурацию из БД
        return Map.of(
            "applications", List.of(),
            "restrictions", Map.of(
                "camera", false,
                "bluetooth", true,
                "wifi", true,
                "gps", true
            ),
            "kioskMode", false,
            "updatePolicy", "auto"
        );
    }
    
    private CommandRequest convertToCommandRequest(Command command) {
    CommandRequest request = new CommandRequest();
    request.setId(command.getId());
    request.setType(command.getType().toString());
    request.setParameters(command.getParameters());
    if (command.getExpiresAt() != null) {
        request.setExpiresAt(command.getExpiresAt().toEpochSecond(java.time.ZoneOffset.UTC));
    }
    return request;
}
public List<DeviceDTO> getAllDevices() {
    return deviceRepository.findAll().stream()
        .map(DeviceDTO::fromEntity)
        .collect(Collectors.toList());
}

public DeviceDTO getDevice(String deviceId) {
    Device device = deviceRepository.findByDeviceId(deviceId)
        .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
    return DeviceDTO.fromEntity(device);
}

public void sendCommand(String deviceId, CommandDTO commandDTO) {
    Command command = new Command();
    command.setDeviceId(deviceId);
    command.setType(commandDTO.getType());
    command.setParameters(commandDTO.getParameters());
    command.setStatus(Command.CommandStatus.PENDING);
    command.setExpiresAt(LocalDateTime.now().plusMinutes(5));
    
    command = commandRepository.save(command);
    
    // Отправляем через MQTT
    mqttPublisher.publishCommand(deviceId, command);
}


public void acknowledgeCommand(String commandId, CommandResult result) {
    commandRepository.findById(commandId).ifPresent(command -> {
        if ("SUCCESS".equals(result.getStatus())) {
            command.setStatus(Command.CommandStatus.COMPLETED);
            command.setResult(result.getResult());
        } else {
            command.setStatus(Command.CommandStatus.FAILED);
            command.setErrorMessage(result.getErrorMessage());
        }
        command.setCompletedAt(LocalDateTime.now());
        commandRepository.save(command);
    });
}
// Для обратной совместимости с hmdm-android
public List<CommandRequest> getHmdmPendingCommands(String deviceId) {
    List<Command> commands = commandRepository.findByDeviceIdAndStatus(
        deviceId, Command.CommandStatus.PENDING);
    
    return commands.stream()
        .map(this::convertToHmdmCommandRequest)
        .collect(Collectors.toList());
}

private CommandRequest convertToHmdmCommandRequest(Command command) {
    CommandRequest request = new CommandRequest();
    request.setId(command.getId());
    request.setType(command.getType().toString());
    request.setParameters(command.getParameters());
    if (command.getExpiresAt() != null) {
        request.setExpiresAt(command.getExpiresAt().toEpochSecond(java.time.ZoneOffset.UTC));
    }
    return request;
}
public List<CommandDTO> getAllCommands(String deviceId) {
    return commandRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId)
        .stream()
        .map(CommandDTO::fromEntity)
        .collect(Collectors.toList());
}
}
