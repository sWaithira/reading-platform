package org.example.readingplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateQuoteRequest(
        @NotBlank @Size(max = 2000) String text,
        Integer pageNumber,
        Boolean isPublic // optional, defaults to false
) {}