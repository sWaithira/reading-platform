package org.example.readingplatform.dto;

import org.example.readingplatform.entity.ReadingStatus;
import org.example.readingplatform.entity.Visibility;

import java.math.BigDecimal;

public record UpdateUserBookRequest(
        ReadingStatus status,
        BigDecimal progressPercent,
        Integer currentPage,
        Integer rating,
        Visibility visibility
) {}