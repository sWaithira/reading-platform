package org.example.readingplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.readingplatform.dto.CreateQuoteRequest;
import org.example.readingplatform.dto.QuoteResponse;
import org.example.readingplatform.dto.UpdateQuoteRequest;
import org.example.readingplatform.entity.Quote;
import org.example.readingplatform.entity.UserBook;
import org.example.readingplatform.entity.Visibility;
import org.example.readingplatform.repository.QuoteRepository;
import org.example.readingplatform.repository.UserBookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final UserBookRepository userBookRepository;

    public QuoteResponse createQuote(UUID userId, UUID userBookId, CreateQuoteRequest request) {
        UserBook userBook = getOwnedUserBook(userId, userBookId);
        boolean makePublic = Boolean.TRUE.equals(request.isPublic());
        requirePublicShelfIfPublic(userBook, makePublic);

        Quote quote = Quote.builder()
                .userBookId(userBook.getId())
                .text(request.text())
                .pageNumber(request.pageNumber())
                .isPublic(makePublic)
                .build();

        return toResponse(quoteRepository.save(quote));
    }

    public List<QuoteResponse> getMyQuotes(UUID userId, UUID userBookId) {
        UserBook userBook = getOwnedUserBook(userId, userBookId);
        return quoteRepository.findByUserBookIdOrderByCreatedAtDesc(userBook.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<QuoteResponse> getPublicQuotesForBook(UUID bookId) {
        return quoteRepository.findPublicByBookId(bookId).stream().map(this::toResponse).toList();
    }

    public QuoteResponse updateQuote(UUID userId, UUID quoteId, UpdateQuoteRequest request) {
        Quote quote = getOwnedQuote(userId, quoteId);

        if (request.text() != null) quote.setText(request.text());
        if (request.pageNumber() != null) quote.setPageNumber(request.pageNumber());
        if (request.isPublic() != null) {
            if (request.isPublic()) {
                UserBook userBook = getOwnedUserBook(userId, quote.getUserBookId());
                requirePublicShelfIfPublic(userBook, true);
            }
            quote.setPublic(request.isPublic());
        }

        return toResponse(quoteRepository.save(quote));
    }

    public void deleteQuote(UUID userId, UUID quoteId) {
        quoteRepository.delete(getOwnedQuote(userId, quoteId));
    }

    private UserBook getOwnedUserBook(UUID userId, UUID userBookId) {
        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        if (!userBook.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"); // same 404 pattern as library
        }
        return userBook;
    }

    private Quote getOwnedQuote(UUID userId, UUID quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        getOwnedUserBook(userId, quote.getUserBookId());
        return quote;
    }

    private void requirePublicShelfIfPublic(UserBook userBook, boolean makePublic) {
        if (makePublic && userBook.getVisibility() != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Set this book's visibility to PUBLIC before making its quotes public");
        }
    }

    private QuoteResponse toResponse(Quote q) {
        return new QuoteResponse(q.getId(), q.getUserBookId(), q.getText(), q.getPageNumber(), q.isPublic(), q.getCreatedAt());
    }
}