package com.doan1.catalog.dto.response;

import com.doan1.catalog.enums.HistoryAction;

import java.time.Instant;
import java.util.UUID;

public record HistoryResponse(
        UUID id,
        UUID packageId,
        UUID actorId,
        UUID adminId,
        HistoryAction action,
        String reason,
        long packageVersion,
        Instant createdAt) {
}
