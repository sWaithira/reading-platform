package org.example.readingplatform.repository;

import org.example.readingplatform.entity.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookTagRepository extends JpaRepository<BookTag, UUID> {
    List<BookTag> findByBookId(UUID bookId);
    Optional<BookTag> findByBookIdAndTagId(UUID bookId, UUID tagId);
}