package com.qmdm.dto;

import com.qmdm.model.Device;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class DeviceDTO {
    private String id;
    private String deviceId;
    private String name;
    private String model;
    private String manufacturer;
    private String androidVersion;
    private Integer apiLevel;
    private Device.DeviceStatus status;
    private LocalDateTime lastSeen;
    private LocalDateTime enrolledAt;
    private String groupId;
    private Map<String, String> properties;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static DeviceDTO fromEntity(Device device) {
        if (device == null) return null;
        
        DeviceDTO dto = new DeviceDTO();
        dto.setId(device.getId());
        dto.setDeviceId(device.getDeviceId());
        dto.setName(device.getName());
        dto.setModel(device.getModel());
        dto.setManufacturer(device.getManufacturer());
        dto.setAndroidVersion(device.getAndroidVersion());
        dto.setApiLevel(device.getApiLevel());
        dto.setStatus(device.getStatus());
        dto.setLastSeen(device.getLastSeen());
        dto.setEnrolledAt(device.getEnrolledAt());
        dto.setGroupId(device.getGroupId());
        dto.setProperties(device.getProperties());
        dto.setCreatedAt(device.getCreatedAt());
        dto.setUpdatedAt(device.getUpdatedAt());
        return dto;
    }
}
