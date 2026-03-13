package com.qmdm.repository;

import com.qmdm.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, String> {
    
    List<Command> findByDeviceIdAndStatus(String deviceId, Command.CommandStatus status);
    
    List<Command> findByDeviceIdOrderByCreatedAtDesc(String deviceId);
    
    @Query("SELECT COUNT(c) FROM Command c WHERE c.createdAt > :since")
    long countByCreatedAtAfter(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(c) FROM Command c WHERE c.status = :status AND c.createdAt > :since")
    long countByStatusAndCreatedAtAfter(
        @Param("status") Command.CommandStatus status, 
        @Param("since") LocalDateTime since);
    
    @Query("SELECT c FROM Command c WHERE c.status = 'PENDING' AND c.expiresAt < :now")
    List<Command> findExpiredCommands(@Param("now") LocalDateTime now);
}
