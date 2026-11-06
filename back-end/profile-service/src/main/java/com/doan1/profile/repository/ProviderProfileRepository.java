package com.doan1.profile.repository;

import com.doan1.profile.entity.ProviderProfile;
import com.doan1.profile.enums.ProviderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {
    @Query("""
            select distinct provider from ProviderProfile provider
            join provider.categoryIds categoryId
            where categoryId = :categoryId and provider.status = :status
              and provider.suspended = false
            order by provider.rating desc, provider.reviewCount desc, provider.fullName asc
            """)
    List<ProviderProfile> findPublicByCategory(
            @Param("categoryId") String categoryId,
            @Param("status") ProviderStatus status,
            Pageable pageable);

    List<ProviderProfile> findByStatusOrderByCreatedAtAsc(ProviderStatus status);
    Optional<ProviderProfile> findByIdentityAccountId(UUID identityAccountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select provider from ProviderProfile provider where provider.id = :id")
    Optional<ProviderProfile> findByIdForUpdate(@Param("id") UUID id);
}
