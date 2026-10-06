package org.example.readingplatform.service;

import org.example.readingplatform.dto.AddToLibraryRequest;
import org.example.readingplatform.dto.UpdateUserBookRequest;
import org.example.readingplatform.dto.UserBookResponse;
import org.example.readingplatform.entity.Book;
import org.example.readingplatform.entity.ReadingStatus;
import org.example.readingplatform.entity.UserBook;
import org.example.readingplatform.entity.Visibility;
import org.example.readingplatform.repository.BookRepository;
import org.example.readingplatform.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;

    public List<UserBookResponse> getLibrary(UUID userId, ReadingStatus statusFilter) {
        List<UserBook> entries = statusFilter != null
                ? userBookRepository.findByUserIdAndStatus(userId, statusFilter)
                : userBookRepository.findByUserId(userId);

        return entries.stream().map(this::toResponse).toList();
    }

    public UserBookResponse addToLibrary(UUID userId, AddToLibraryRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        UserBook userBook = UserBook.builder()
                .userId(userId)
                .bookId(book.getId())
                .status(request.status() != null ? request.status() : ReadingStatus.WANT_TO_READ)
                .visibility(Visibility.PRIVATE)
                .build();

        if (userBook.getStatus() == ReadingStatus.READING) {
            userBook.setStartedAt(Instant.now());
        }

        userBookRepository.save(userBook);
        return toResponse(userBook, book);
    }

    public UserBookResponse updateUserBook(UUID userId, UUID userBookId, UpdateUserBookRequest request) {
        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        if (!userBook.getUserId().equals(userId)) {
            // 404, not 403 - don't reveal that a userBook exists on someone else's shelf
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }

        if (request.status() != null) {
            userBook.setStatus(request.status());
            if (request.status() == ReadingStatus.READING && userBook.getStartedAt() == null) {
                userBook.setStartedAt(Instant.now());
            }
            if (request.status() == ReadingStatus.FINISHED && userBook.getFinishedAt() == null) {
                userBook.setFinishedAt(Instant.now());
            }
        }
        if (request.progressPercent() != null) userBook.setProgressPercent(request.progressPercent());
        if (request.currentPage() != null) userBook.setCurrentPage(request.currentPage());
        if (request.rating() != null) userBook.setRating(request.rating());
        if (request.visibility() != null) userBook.setVisibility(request.visibility());

        userBookRepository.save(userBook);

        Book book = bookRepository.findById(userBook.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return toResponse(userBook, book);
    }

    public void deleteUserBook(UUID userId, UUID userBookId) {
        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        if (!userBook.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }

        userBookRepository.delete(userBook);
    }

    private UserBookResponse toResponse(UserBook userBook) {
        Book book = bookRepository.findById(userBook.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return toResponse(userBook, book);
    }

    private UserBookResponse toResponse(UserBook userBook, Book book) {
        return new UserBookResponse(
                userBook.getId(), book.getId(), book.getTitle(), book.getAuthor(), book.getCoverUrl(),
                userBook.getStatus(), userBook.getProgressPercent(), userBook.getCurrentPage(),
                userBook.getStartedAt(), userBook.getFinishedAt(), userBook.getRating(), userBook.getVisibility()
        );
    }
}