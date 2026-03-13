package com.qmdm.service;

import com.qmdm.dto.auth.LoginRequest;
import com.qmdm.dto.auth.LoginResponse;
import com.qmdm.dto.auth.RegisterRequest;
import com.qmdm.model.User;
import com.qmdm.model.Role;
import com.qmdm.repository.UserRepository;
import com.qmdm.repository.RoleRepository;
import com.qmdm.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Проверяем, существует ли пользователь
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Создаем нового пользователя
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());

        // Назначаем роль USER по умолчанию
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(userRole));

        user = userRepository.save(user);

        // Генерируем JWT токен
        String token = jwtService.generateToken(user);
        
        return LoginResponse.builder()
            .token(token)
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(user.getRoles().stream().map(Role::getName).toList())
            .build();
    }

    public LoginResponse login(LoginRequest request) {
        // Аутентифицируем пользователя
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // Получаем пользователя из БД
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Генерируем JWT токен
        String token = jwtService.generateToken(user);
        
        return LoginResponse.builder()
            .token(token)
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(user.getRoles().stream().map(Role::getName).toList())
            .build();
    }
}
