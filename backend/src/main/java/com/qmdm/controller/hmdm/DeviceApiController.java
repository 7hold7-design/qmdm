package com.qmdm.controller.hmdm;

import com.qmdm.dto.hmdm.DeviceRegistrationRequest;
import com.qmdm.dto.hmdm.DeviceConfigurationResponse;
import com.qmdm.dto.hmdm.CommandRequest;
import com.qmdm.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hmdm/v1")
@RequiredArgsConstructor
@Slf4j
public class DeviceApiController {
    
    private final DeviceService deviceService;
    
    @PostMapping("/device/register")
    public ResponseEntity<?> registerDevice(@RequestBody DeviceRegistrationRequest request) {
        log.info("Device registration request: {}", request);
        // Эмуляция ответа оригинального hmdm-server
        return ResponseEntity.ok(deviceService.registerDevice(request));
    }
    
    @GetMapping("/device/{deviceId}/configuration")
    public ResponseEntity<DeviceConfigurationResponse> getConfiguration(@PathVariable String deviceId) {
        log.info("Getting configuration for device: {}", deviceId);
        return ResponseEntity.ok(deviceService.getDeviceConfiguration(deviceId));
    }
    
    @PostMapping("/device/{deviceId}/report")
    public ResponseEntity<?> reportDeviceStatus(@PathVariable String deviceId,
                                                @RequestBody Map<String, Object> report) {
        log.info("Device report from {}: {}", deviceId, report);
        deviceService.processDeviceReport(deviceId, report);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/device/{deviceId}/commands")
    public ResponseEntity<List<CommandRequest>> getPendingCommands(@PathVariable String deviceId) {
        log.info("Getting pending commands for device: {}", deviceId);
        //return ResponseEntity.ok(deviceService.getPendingCommands(deviceId));
         return ResponseEntity.ok(deviceService.getHmdmPendingCommands(deviceId));
    }
    
    @PostMapping("/device/{deviceId}/commands/{commandId}/ack")
    public ResponseEntity<?> acknowledgeCommand(@PathVariable String deviceId,
                                                @PathVariable String commandId,
                                                @RequestBody Map<String, Object> result) {
        log.info("Command {} acknowledged by device {} with result: {}", commandId, deviceId, result);
        deviceService.updateCommandStatus(commandId, result);
        return ResponseEntity.ok().build();
    }
}
