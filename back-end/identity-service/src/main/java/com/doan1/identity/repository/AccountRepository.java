package com.doan1.identity.repository;

import com.doan1.identity.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByPhoneNumber(String phoneNumber);
}
