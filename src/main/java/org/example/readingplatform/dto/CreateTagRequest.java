package org.example.readingplatform.dto;

import org.example.readingplatform.entity.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTagRequest(
        @NotBlank String name,
        @NotNull Tag.TagType type
) {}