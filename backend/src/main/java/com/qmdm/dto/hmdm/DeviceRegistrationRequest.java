package com.qmdm.dto.hmdm;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@Data
public class DeviceRegistrationRequest {
    
    @JsonProperty("deviceId")
    private String deviceId;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("manufacturer")
    private String manufacturer;
    
    @JsonProperty("androidVersion")
    private String androidVersion;
    
    @JsonProperty("apiLevel")
    private Integer apiLevel;
    
    @JsonProperty("enrollmentToken")
    private String enrollmentToken;
    
    @JsonProperty("properties")
    private Map<String, String> properties;
}
