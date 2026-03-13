package com.qmdm.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long totalDevices;
    private long activeDevices;
    private long pendingDevices;
    private long suspendedDevices;
    private long retiredDevices;
    private long recentCommands;
    private long failedCommands;
}
