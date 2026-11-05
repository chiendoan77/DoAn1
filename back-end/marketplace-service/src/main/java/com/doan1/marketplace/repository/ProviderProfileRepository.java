package com.doan1.marketplace.repository;

import com.doan1.marketplace.model.ProviderProfile;
import com.doan1.marketplace.model.ProviderStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {
    @Query("""
            select distinct provider from ProviderProfile provider
            join provider.categoryIds categoryId
            where categoryId = :categoryId and provider.status = :status
            order by provider.rating desc, provider.reviewCount desc, provider.fullName asc
            """)
    List<ProviderProfile> findByCategoryIdAndStatus(
            @Param("categoryId") String categoryId,
            @Param("status") ProviderStatus status,
            Pageable pageable);

    List<ProviderProfile> findByStatusOrderByCreatedAtAsc(ProviderStatus status);

    Optional<ProviderProfile> findByIdentityAccountId(UUID identityAccountId);

    boolean existsByIdentityAccountId(UUID identityAccountId);

    boolean existsByDemoFixtureTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select provider from ProviderProfile provider where provider.id = :id")
    Optional<ProviderProfile> findByIdForUpdate(@Param("id") UUID id);
}
