package org.example.readingplatform.dto;

import org.example.readingplatform.entity.Tag;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        Tag.TagType type,
        boolean isSystem
) {}