package org.example.readingplatform.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadingSessionResponse(
        UUID id,
        UUID userBookId,
        Integer durationMinutes,
        Integer pagesRead,
        Instant loggedAt
) {}