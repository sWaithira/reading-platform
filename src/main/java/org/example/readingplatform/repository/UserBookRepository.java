package org.example.readingplatform.repository;

import org.example.readingplatform.entity.ReadingStatus;
import org.example.readingplatform.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserBookRepository extends JpaRepository<UserBook, UUID> {
    List<UserBook> findByUserId(UUID userId);
    List<UserBook> findByUserIdAndStatus(UUID userId, ReadingStatus status);
}