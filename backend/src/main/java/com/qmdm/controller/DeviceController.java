package com.qmdm.controller;

import com.qmdm.dto.DeviceDTO;
import com.qmdm.dto.CommandDTO;
import com.qmdm.dto.CommandResult;
import com.qmdm.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {
    
    private final DeviceService deviceService;
    
    @GetMapping
    @PreAuthorize("hasAuthority('device:read')")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        log.info("Getting all devices");
        return ResponseEntity.ok(deviceService.getAllDevices());
    }
    
    @GetMapping("/{deviceId}")
    @PreAuthorize("hasAuthority('device:read')")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable String deviceId) {
        log.info("Getting device: {}", deviceId);
        return ResponseEntity.ok(deviceService.getDevice(deviceId));
    }
    
    @PostMapping("/{deviceId}/commands")
    @PreAuthorize("hasAuthority('command:send')")
    public ResponseEntity<Void> sendCommand(
            @PathVariable String deviceId,
            @RequestBody CommandDTO command) {
        log.info("Sending command to device {}: {}", deviceId, command.getType());
        deviceService.sendCommand(deviceId, command);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping("/{deviceId}/commands")
    @PreAuthorize("hasAuthority('device:read')")
    public ResponseEntity<List<CommandDTO>> getDeviceCommands(@PathVariable String deviceId) {
        log.info("Getting all commands for device: {}", deviceId);
        // Здесь можно вернуть все команды, не только PENDING
        return ResponseEntity.ok(deviceService.getAllCommands(deviceId));
    }
    
    @GetMapping("/{deviceId}/commands/pending")
    @PreAuthorize("hasAuthority('device:read')")
    public ResponseEntity<List<CommandDTO>> getPendingCommands(@PathVariable String deviceId) {
        log.info("Getting pending commands for device: {}", deviceId);
        return ResponseEntity.ok(deviceService.getPendingCommands(deviceId));
    }
    
    @PostMapping("/{deviceId}/commands/{commandId}/ack")
    @PreAuthorize("hasAuthority('device:write')")
    public ResponseEntity<Void> acknowledgeCommand(
            @PathVariable String deviceId,
            @PathVariable String commandId,
            @RequestBody CommandResult result) {
        log.info("Command {} acknowledged by device {} with status: {}", commandId, deviceId, result.getStatus());
        deviceService.acknowledgeCommand(commandId, result);
        return ResponseEntity.ok().build();
    }
}
