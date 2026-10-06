package org.example.readingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String isbn;

    private String coverUrl;

    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookSource source;

    // Null for catalog books; set when a user adds a book manually / via upload
    private UUID createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public enum BookSource {
        OPEN_LIBRARY,
        USER_UPLOAD,
        MANUAL
    }
}