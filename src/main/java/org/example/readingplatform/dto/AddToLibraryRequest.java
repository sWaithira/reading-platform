package org.example.readingplatform.dto;

import org.example.readingplatform.entity.ReadingStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddToLibraryRequest(
        @NotNull UUID bookId,
        ReadingStatus status // optional; defaults to WANT_TO_READ if null
) {}