package com.qmdm.dto;

import com.qmdm.model.Command;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class CommandDTO {
    private String id;
    private String deviceId;
    private Command.CommandType type;
    private Map<String, Object> parameters;
    private Command.CommandStatus status;
    private Map<String, Object> result;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    
    public static CommandDTO fromEntity(Command command) {
        if (command == null) return null;
        
        CommandDTO dto = new CommandDTO();
        dto.setId(command.getId());
        dto.setDeviceId(command.getDeviceId());
        dto.setType(command.getType());
        dto.setParameters(command.getParameters());
        dto.setStatus(command.getStatus());
        dto.setResult(command.getResult());
        dto.setErrorMessage(command.getErrorMessage());
        dto.setSentAt(command.getSentAt());
        dto.setCompletedAt(command.getCompletedAt());
        dto.setCreatedAt(command.getCreatedAt());
        return dto;
    }
}
