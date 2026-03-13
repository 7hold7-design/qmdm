package com.qmdm.controller;

import com.qmdm.dto.auth.LoginRequest;
import com.qmdm.dto.auth.LoginResponse;
import com.qmdm.dto.auth.RegisterRequest;
import com.qmdm.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("========== LOGIN ATTEMPT ==========");
        log.info("Username: {}", request.getUsername());
        
        try {
            LoginResponse response = authService.login(request);
            log.info("Login successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", request.getUsername());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (DisabledException e) {
            log.error("User disabled: {}", request.getUsername());
            Map<String, String> error = new HashMap<>();
            error.put("error", "User account is disabled");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            
        } catch (LockedException e) {
            log.error("User locked: {}", request.getUsername());
            Map<String, String> error = new HashMap<>();
            error.put("error", "User account is locked");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        
        try {
            LoginResponse response = authService.register(request);
            log.info("Registration successful for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            log.error("Registration error", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
