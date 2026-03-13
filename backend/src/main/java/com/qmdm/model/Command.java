package com.qmdm.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "commands")
@Data
@NoArgsConstructor
public class Command {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String deviceId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandType type;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> parameters;
    
    @Enumerated(EnumType.STRING)
    private CommandStatus status = CommandStatus.PENDING;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> result;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum CommandType {
        INSTALL_APP, UNINSTALL_APP, UPDATE_APP,
        LOCK_DEVICE, WIPE_DEVICE, REBOOT,
        UPDATE_CONFIGURATION, GET_DEVICE_INFO,
        CLEAR_APP_DATA, ENABLE_KIOSK_MODE, DISABLE_KIOSK_MODE
    }
    
    public enum CommandStatus {
        PENDING, SENT, DELIVERED, COMPLETED, FAILED, EXPIRED
    }
}
