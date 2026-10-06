package org.example.readingplatform.controller;

import org.example.readingplatform.entity.Book;
import org.example.readingplatform.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    // Minimal manual-add endpoint, just enough to test library CRUD against a real book.
    // Full search/catalog endpoints (Open Library integration, uploads) come in a later slice.
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        book.setSource(Book.BookSource.MANUAL);
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable UUID id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
    }
}