package com.qmdm.config;

import com.qmdm.model.Role;
import com.qmdm.model.User;
import com.qmdm.repository.RoleRepository;
import com.qmdm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создаем тестового администратора, если его нет
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@qmdm.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRoles(Set.of(adminRole));
            
            userRepository.save(admin);
            log.info("Created test admin user: admin/admin123");
        }
    }
}
