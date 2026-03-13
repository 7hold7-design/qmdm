package com.qmdm.service;

import com.qmdm.dto.DashboardStatsDTO;
import com.qmdm.model.Device;
import com.qmdm.model.Command;
import com.qmdm.repository.DeviceRepository;
import com.qmdm.repository.CommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {
    
    private final DeviceRepository deviceRepository;
    private final CommandRepository commandRepository;
    
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // Статистика устройств
        stats.setTotalDevices(deviceRepository.count());
        stats.setActiveDevices(deviceRepository.countByStatus(Device.DeviceStatus.ACTIVE));
        stats.setPendingDevices(deviceRepository.countByStatus(Device.DeviceStatus.PENDING));
        stats.setSuspendedDevices(deviceRepository.countByStatus(Device.DeviceStatus.SUSPENDED));
        stats.setRetiredDevices(deviceRepository.countByStatus(Device.DeviceStatus.RETIRED));
        
        // Статистика команд за последние 24 часа
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        stats.setRecentCommands(commandRepository.countByCreatedAtAfter(last24Hours));
        stats.setFailedCommands(commandRepository.countByStatusAndCreatedAtAfter(
            Command.CommandStatus.FAILED, last24Hours));
        
        log.info("Dashboard stats retrieved: {}", stats);
        return stats;
    }
}
