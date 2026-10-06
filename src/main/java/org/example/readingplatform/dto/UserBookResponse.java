package org.example.readingplatform.dto;

import org.example.readingplatform.entity.ReadingStatus;
import org.example.readingplatform.entity.Visibility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UserBookResponse(
        UUID id,
        UUID bookId,
        String title,
        String author,
        String coverUrl,
        ReadingStatus status,
        BigDecimal progressPercent,
        Integer currentPage,
        Instant startedAt,
        Instant finishedAt,
        Integer rating,
        Visibility visibility
) {}