package org.example.readingplatform.dto;

import jakarta.validation.constraints.Min;

import java.time.Instant;

public record LogSessionRequest(
        @Min(1) Integer durationMinutes,
        @Min(1) Integer pagesRead,
        Instant loggedAt // optional, for backfilling past sessions; defaults to now
) {}