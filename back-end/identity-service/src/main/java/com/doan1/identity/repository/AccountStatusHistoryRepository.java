package com.doan1.identity.repository;

import com.doan1.identity.entity.AccountStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, UUID> {
}
