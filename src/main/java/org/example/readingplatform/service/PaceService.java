package org.example.readingplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.readingplatform.dto.PaceResponse;
import org.example.readingplatform.dto.PaceResponse.BookProjection;
import org.example.readingplatform.entity.Book;
import org.example.readingplatform.entity.ReadingSession;
import org.example.readingplatform.entity.ReadingStatus;
import org.example.readingplatform.entity.UserBook;
import org.example.readingplatform.repository.BookRepository;
import org.example.readingplatform.repository.ReadingSessionRepository;
import org.example.readingplatform.repository.UserBookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaceService {

    private static final int WINDOW_DAYS = 30;

    private final ReadingSessionRepository readingSessionRepository;
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;

    public PaceResponse getPace(UUID userId) {
        Instant now = Instant.now();
        List<ReadingSession> sessions =
                readingSessionRepository.findByUserSince(userId, now.minus(WINDOW_DAYS, ChronoUnit.DAYS));

        // Pages per day: total pages / calendar days since the first session in the window.
        // Calendar days (not reading days) on purpose, so skipped days lower the pace honestly.
        List<ReadingSession> withPages = sessions.stream().filter(s -> s.getPagesRead() != null).toList();
        int totalPages = withPages.stream().mapToInt(ReadingSession::getPagesRead).sum();

        BigDecimal avgPagesPerDay;
        Integer windowDays = null;
        if (totalPages > 0) {
            Instant earliest = withPages.stream().map(ReadingSession::getLoggedAt).min(Comparator.naturalOrder()).get();
            long days = Math.max(1, ChronoUnit.DAYS.between(earliest, now) + 1);
            windowDays = (int) days;
            avgPagesPerDay = BigDecimal.valueOf(totalPages).divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
        } else {
            avgPagesPerDay = null;
        }

        // Minutes per page: only from sessions that recorded both, otherwise the ratio is meaningless.
        List<ReadingSession> withBoth = sessions.stream()
                .filter(s -> s.getPagesRead() != null && s.getDurationMinutes() != null).toList();
        int bothPages = withBoth.stream().mapToInt(ReadingSession::getPagesRead).sum();
        int bothMinutes = withBoth.stream().mapToInt(ReadingSession::getDurationMinutes).sum();
        BigDecimal avgMinutesPerPage = bothPages > 0
                ? BigDecimal.valueOf(bothMinutes).divide(BigDecimal.valueOf(bothPages), 2, RoundingMode.HALF_UP)
                : null;

        List<BookProjection> projections = userBookRepository.findByUserIdAndStatus(userId, ReadingStatus.READING)
                .stream()
                .map(ub -> project(ub, avgPagesPerDay, avgMinutesPerPage))
                .toList();

        return new PaceResponse(avgPagesPerDay, windowDays, avgMinutesPerPage, projections);
    }

    private BookProjection project(UserBook ub, BigDecimal avgPagesPerDay, BigDecimal avgMinutesPerPage) {
        Book book = bookRepository.findById(ub.getBookId()).orElse(null);
        if (book == null || book.getPageCount() == null) {
            return new BookProjection(ub.getId(), ub.getBookId(), book != null ? book.getTitle() : null,
                    null, null, null, null);
        }

        int pagesRemaining = Math.max(0, book.getPageCount() - currentPage(ub, book));

        Integer daysRemaining = null;
        LocalDate finishDate = null;
        if (avgPagesPerDay != null) {
            daysRemaining = BigDecimal.valueOf(pagesRemaining)
                    .divide(avgPagesPerDay, 0, RoundingMode.CEILING).intValue();
            finishDate = LocalDate.now(ZoneOffset.UTC).plusDays(daysRemaining);
        }

        Integer minutesRemaining = avgMinutesPerPage != null
                ? avgMinutesPerPage.multiply(BigDecimal.valueOf(pagesRemaining)).setScale(0, RoundingMode.HALF_UP).intValue()
                : null;

        return new BookProjection(ub.getId(), ub.getBookId(), book.getTitle(),
                pagesRemaining, daysRemaining, finishDate, minutesRemaining);
    }

    // Prefer the exact page; fall back to progress percentage of the page count.
    private int currentPage(UserBook ub, Book book) {
        if (ub.getCurrentPage() != null) return ub.getCurrentPage();
        if (ub.getProgressPercent() != null) {
            return ub.getProgressPercent()
                    .multiply(BigDecimal.valueOf(book.getPageCount()))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP).intValue();
        }
        return 0;
    }
}