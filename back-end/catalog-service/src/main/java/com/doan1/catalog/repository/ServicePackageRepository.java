package com.doan1.catalog.repository;

import com.doan1.catalog.entity.ServicePackage;
import com.doan1.catalog.enums.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServicePackageRepository
        extends JpaRepository<ServicePackage, UUID>, JpaSpecificationExecutor<ServicePackage> {
    Optional<ServicePackage> findByIdAndOwnerAccountId(UUID id, UUID ownerAccountId);

    boolean existsByCategoryIdAndStatus(String categoryId, PackageStatus status);
}
