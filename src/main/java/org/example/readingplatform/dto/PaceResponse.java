package org.example.readingplatform.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PaceResponse(
        BigDecimal avgPagesPerDay,    // null until the user has logged at least one session with pages
        Integer windowDays,           // how many days the average was spread over
        BigDecimal avgMinutesPerPage, // null until a session has both pages and minutes
        List<BookProjection> projections
) {
    public record BookProjection(
            UUID userBookId,
            UUID bookId,
            String title,
            Integer pagesRemaining,          // null if the book has no page count
            Integer estimatedDaysRemaining,  // null if pace or page count is unknown
            LocalDate estimatedFinishDate,
            Integer estimatedMinutesRemaining
    ) {}
}