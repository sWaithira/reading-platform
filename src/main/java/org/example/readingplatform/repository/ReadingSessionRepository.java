package org.example.readingplatform.repository;

import org.example.readingplatform.entity.ReadingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadingSessionRepository extends JpaRepository<ReadingSession, UUID> {

    List<ReadingSession> findByUserBookIdOrderByLoggedAtDesc(UUID userBookId);

    // All of a user's sessions across every book since a point in time (feeds pace calculation)
    @Query("""
            select s from ReadingSession s
            where s.loggedAt >= :since
              and s.userBookId in (select ub.id from UserBook ub where ub.userId = :userId)
            """)
    List<ReadingSession> findByUserSince(@Param("userId") UUID userId, @Param("since") Instant since);
}