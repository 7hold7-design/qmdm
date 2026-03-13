package com.qmdm.dto.hmdm;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@Data
public class DeviceConfigurationResponse {
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("configuration")
    private Map<String, Object> configuration;
    
    @JsonProperty("applications")
    private List<Map<String, Object>> applications;
    
    @JsonProperty("restrictions")
    private Map<String, Boolean> restrictions;
    
    @JsonProperty("kioskMode")
    private Boolean kioskMode;
    
    @JsonProperty("updatePolicy")
    private String updatePolicy;
}
