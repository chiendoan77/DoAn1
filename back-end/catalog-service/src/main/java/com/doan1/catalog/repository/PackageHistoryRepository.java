package com.doan1.catalog.repository;

import com.doan1.catalog.entity.PackageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PackageHistoryRepository extends JpaRepository<PackageHistory, UUID> {
    List<PackageHistory> findAllByPackageIdOrderByCreatedAtAsc(UUID packageId);
}
