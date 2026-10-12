package org.example.readingplatform.dto;

import jakarta.validation.constraints.Size;

public record UpdateQuoteRequest(
        @Size(min = 1, max = 2000) String text,
        Integer pageNumber,
        Boolean isPublic
) {}