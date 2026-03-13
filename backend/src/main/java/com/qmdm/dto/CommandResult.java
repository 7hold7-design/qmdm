package com.qmdm.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CommandResult {
    private String commandId;
    private String status;
    private Map<String, Object> result;
    private String errorMessage;
}
