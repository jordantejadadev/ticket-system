package com.jordan.ticket_system.config;

import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initUsers(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if(repo.findByEmail("admin@test.com").isEmpty()) {

                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@test.com");
                admin.setPassword(encoder.encode("123456"));
                admin.setRole(Role.ADMIN);

                repo.save(admin);
            }
        };
    }
}
