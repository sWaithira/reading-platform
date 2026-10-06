package org.example.readingplatform.repository;

import org.example.readingplatform.entity.BookFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookFileRepository extends JpaRepository<BookFile, UUID> {
    List<BookFile> findByBookIdAndUserId(UUID bookId, UUID userId);
}