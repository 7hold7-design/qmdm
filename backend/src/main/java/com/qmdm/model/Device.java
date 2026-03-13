package com.qmdm.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String deviceId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false)
    private String manufacturer;
    
    @Column(name = "android_version")
    private String androidVersion;
    
    @Column(name = "api_level")
    private Integer apiLevel;
    
    @Enumerated(EnumType.STRING)
    private DeviceStatus status = DeviceStatus.PENDING;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "enrollment_token")
    private String enrollmentToken;
    
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;
    
    @Column(name = "group_id")
    private String groupId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> properties = new HashMap<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    public enum DeviceStatus {
        PENDING, ACTIVE, SUSPENDED, RETIRED
    }
}
