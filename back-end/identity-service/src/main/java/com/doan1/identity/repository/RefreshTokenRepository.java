package com.doan1.identity.repository;

import com.doan1.identity.entity.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshToken token where token.tokenHash = :tokenHash")
    Optional<RefreshToken> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    @Query("update RefreshToken token set token.revokedAt = :revokedAt "
            + "where token.familyId = :familyId and token.revokedAt is null")
    @org.springframework.data.jpa.repository.Modifying
    int revokeActiveFamily(@Param("familyId") UUID familyId, @Param("revokedAt") Instant revokedAt);

    @org.springframework.data.jpa.repository.Modifying
    @Query("update RefreshToken token set token.revokedAt = :revokedAt "
            + "where token.accountId = :accountId and token.revokedAt is null")
    int revokeActiveForAccount(@Param("accountId") UUID accountId, @Param("revokedAt") Instant revokedAt);

    @org.springframework.data.jpa.repository.Modifying
    @Query("delete from RefreshToken token where token.accountId = :accountId")
    int deleteAllForAccount(@Param("accountId") UUID accountId);
}
