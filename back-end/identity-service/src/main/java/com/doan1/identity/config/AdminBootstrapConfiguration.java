package com.doan1.identity.config;

import com.doan1.identity.model.Account;
import com.doan1.identity.model.Role;
import com.doan1.identity.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Locale;

@Configuration
@ConditionalOnProperty(name = "app.bootstrap-admin.enabled", havingValue = "true")
public class AdminBootstrapConfiguration {
    @Bean
    CommandLineRunner bootstrapAdmin(
            AccountRepository accounts,
            @Value("${app.bootstrap-admin.email}") String email,
            @Value("${app.bootstrap-admin.phone}") String phone,
            @Value("${app.bootstrap-admin.username}") String username,
            @Value("${app.bootstrap-admin.password}") String password,
            @Value("${app.bootstrap-admin.full-name}") String fullName) {
        return args -> {
            String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (accounts.existsByEmailIgnoreCase(normalizedEmail)) {
                return;
            }
            if (email.isBlank() || phone.isBlank() || username.isBlank() || password.length() < 12) {
                throw new IllegalStateException("Bootstrap Admin requires email, phone, username and a password of at least 12 characters.");
            }
            accounts.save(new Account(
                    normalizedEmail,
                    phone.replaceAll("[\\s()-]", ""),
                    username.trim(),
                    fullName.trim(),
                    new BCryptPasswordEncoder().encode(password),
                    Role.ADMIN));
        };
    }
}
