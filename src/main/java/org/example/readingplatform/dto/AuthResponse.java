package org.example.readingplatform.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String displayName
) {}