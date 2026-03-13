package com.qmdm.dto.hmdm;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@Data
public class CommandRequest {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("parameters")
    private Map<String, Object> parameters;
    
    @JsonProperty("expiresAt")
    private Long expiresAt;
}
