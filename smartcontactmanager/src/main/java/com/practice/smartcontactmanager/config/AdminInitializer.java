package com.practice.smartcontactmanager.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.practice.smartcontactmanager.dao.UserRepository;
import com.practice.smartcontactmanager.entities.User;

@Configuration
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            // ✅ Fallback values if ENV not provided
            String adminEmail = System.getenv("ADMIN_EMAIL") != null ? System.getenv("ADMIN_EMAIL") : "yugeshkumar1022003@gmail.com";
            String adminPassword = System.getenv("ADMIN_PASSWORD") != null ? System.getenv("ADMIN_PASSWORD") : "yugesh@7492";

            User existing = userRepository.getUserByUserName(adminEmail);

            if (existing == null) {
                User admin = new User();
                admin.setName("Super Admin");
                admin.setEmail(adminEmail);
                // ❌ No BCrypt, store plain text (only for testing)
                admin.setPassword(adminPassword);
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("✅ Admin account created (plain password): " + adminEmail);
            } else {
                if (!"ROLE_ADMIN".equals(existing.getRole())) {
                    existing.setRole("ROLE_ADMIN");
                    existing.setPassword(adminPassword); // reset password plain
                    userRepository.save(existing);
                    System.out.println("🔄 Existing user upgraded to Admin with plain password: " + adminEmail);
                } else {
                    System.out.println("ℹ️ Admin already exists, skipping creation.");
                }
            }
        };
    }
}
