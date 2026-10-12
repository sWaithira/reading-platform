package org.example.readingplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.readingplatform.dto.CreateQuoteRequest;
import org.example.readingplatform.dto.QuoteResponse;
import org.example.readingplatform.dto.UpdateQuoteRequest;
import org.example.readingplatform.entity.User;
import org.example.readingplatform.security.AuthenticatedUserResolver;
import org.example.readingplatform.service.QuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    @PostMapping("/library/{userBookId}/quotes")
    public ResponseEntity<QuoteResponse> createQuote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userBookId,
            @Valid @RequestBody CreateQuoteRequest request) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(quoteService.createQuote(user.getId(), userBookId, request));
    }

    @GetMapping("/library/{userBookId}/quotes")
    public ResponseEntity<List<QuoteResponse>> getMyQuotes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userBookId) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(quoteService.getMyQuotes(user.getId(), userBookId));
    }

    @GetMapping("/books/{bookId}/quotes")
    public ResponseEntity<List<QuoteResponse>> getPublicQuotes(@PathVariable UUID bookId) {
        return ResponseEntity.ok(quoteService.getPublicQuotesForBook(bookId));
    }

    @PatchMapping("/quotes/{quoteId}")
    public ResponseEntity<QuoteResponse> updateQuote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID quoteId,
            @Valid @RequestBody UpdateQuoteRequest request) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(quoteService.updateQuote(user.getId(), quoteId, request));
    }

    @DeleteMapping("/quotes/{quoteId}")
    public ResponseEntity<Void> deleteQuote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID quoteId) {
        User user = authenticatedUserResolver.resolve(userDetails);
        quoteService.deleteQuote(user.getId(), quoteId);
        return ResponseEntity.noContent().build();
    }
}