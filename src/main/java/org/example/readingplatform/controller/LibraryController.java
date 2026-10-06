package org.example.readingplatform.controller;

import org.example.readingplatform.dto.AddToLibraryRequest;
import org.example.readingplatform.dto.UpdateUserBookRequest;
import org.example.readingplatform.dto.UserBookResponse;
import org.example.readingplatform.entity.ReadingStatus;
import org.example.readingplatform.entity.User;
import org.example.readingplatform.security.AuthenticatedUserResolver;
import org.example.readingplatform.service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    @GetMapping
    public ResponseEntity<List<UserBookResponse>> getLibrary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ReadingStatus status) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(libraryService.getLibrary(user.getId(), status));
    }

    @PostMapping
    public ResponseEntity<UserBookResponse> addToLibrary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToLibraryRequest request) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(libraryService.addToLibrary(user.getId(), request));
    }

    @PatchMapping("/{userBookId}")
    public ResponseEntity<UserBookResponse> updateUserBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userBookId,
            @RequestBody UpdateUserBookRequest request) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(libraryService.updateUserBook(user.getId(), userBookId, request));
    }

    @DeleteMapping("/{userBookId}")
    public ResponseEntity<Void> deleteUserBook(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userBookId) {
        User user = authenticatedUserResolver.resolve(userDetails);
        libraryService.deleteUserBook(user.getId(), userBookId);
        return ResponseEntity.noContent().build();
    }
}