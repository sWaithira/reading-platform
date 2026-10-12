package org.example.readingplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.readingplatform.dto.LogSessionRequest;
import org.example.readingplatform.dto.PaceResponse;
import org.example.readingplatform.dto.ReadingSessionResponse;
import org.example.readingplatform.entity.User;
import org.example.readingplatform.security.AuthenticatedUserResolver;
import org.example.readingplatform.service.PaceService;
import org.example.readingplatform.service.ReadingSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReadingSessionController {

    private final ReadingSessionService readingSessionService;
    private final PaceService paceService;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    @PostMapping("/library/{userBookId}/sessions")
    public ResponseEntity<ReadingSessionResponse> logSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userBookId,
            @Valid @RequestBody LogSessionRequest request) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(readingSessionService.logSession(user.getId(), userBookId, request));
    }

    @GetMapping("/library/{userBookId}/sessions")
    public ResponseEntity<List<ReadingSessionResponse>> getSessions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userBookId) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(readingSessionService.getSessions(user.getId(), userBookId));
    }

    @GetMapping("/users/me/pace")
    public ResponseEntity<PaceResponse> getPace(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(paceService.getPace(user.getId()));
    }
}