package com.qmdm.repository;

import com.qmdm.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    
    Optional<Device> findByDeviceId(String deviceId);
    
    List<Device> findByStatus(Device.DeviceStatus status);
    
    @Query("SELECT d FROM Device d WHERE d.lastSeen < :timeout")
    List<Device> findInactiveDevices(@Param("timeout") LocalDateTime timeout);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = :status")
    long countByStatus(@Param("status") Device.DeviceStatus status);
    
    boolean existsByDeviceId(String deviceId);
    
    void deleteByDeviceId(String deviceId);
}
