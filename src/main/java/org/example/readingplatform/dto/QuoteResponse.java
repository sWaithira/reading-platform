package org.example.readingplatform.dto;

import java.time.Instant;
import java.util.UUID;

public record QuoteResponse(
        UUID id,
        UUID userBookId,
        String text,
        Integer pageNumber,
        boolean isPublic,
        Instant createdAt
) {}