package org.example.readingplatform.repository;

import org.example.readingplatform.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuoteRepository extends JpaRepository<Quote, UUID> {

    List<Quote> findByUserBookIdOrderByCreatedAtDesc(UUID userBookId);

    // Public quotes for a book, only from shelf entries that are still PUBLIC.
    // Re-checking visibility here means flipping a book back to private hides its quotes.
    @Query("""
            select q from Quote q
            where q.isPublic = true
              and q.userBookId in (
                  select ub.id from UserBook ub
                  where ub.bookId = :bookId and ub.visibility = org.example.readingplatform.entity.Visibility.PUBLIC
              )
            order by q.createdAt desc
            """)
    List<Quote> findPublicByBookId(@Param("bookId") UUID bookId);
}