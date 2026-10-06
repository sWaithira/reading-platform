package org.example.readingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "book_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private UUID userId; // uploader - files are private to them unless shared via the book record

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Column(nullable = false)
    private String storageKey; // opaque key from FileStorageService, not a public URL

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private long fileSizeBytes;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @PrePersist
    void onCreate() {
        this.uploadedAt = Instant.now();
    }

    public enum FileType {
        PDF, EPUB, ARTICLE
    }
}