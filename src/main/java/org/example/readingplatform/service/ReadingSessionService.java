package org.example.readingplatform.service;

import lombok.RequiredArgsConstructor;
import org.example.readingplatform.dto.LogSessionRequest;
import org.example.readingplatform.dto.ReadingSessionResponse;
import org.example.readingplatform.entity.ReadingSession;
import org.example.readingplatform.entity.UserBook;
import org.example.readingplatform.repository.ReadingSessionRepository;
import org.example.readingplatform.repository.UserBookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadingSessionService {

    private final ReadingSessionRepository readingSessionRepository;
    private final UserBookRepository userBookRepository;

    public ReadingSessionResponse logSession(UUID userId, UUID userBookId, LogSessionRequest request) {
        UserBook userBook = getOwnedUserBook(userId, userBookId);

        if (request.durationMinutes() == null && request.pagesRead() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide durationMinutes, pagesRead, or both");
        }

        Instant now = Instant.now();
        Instant loggedAt = request.loggedAt() != null ? request.loggedAt() : now;
        if (loggedAt.isAfter(now.plusSeconds(300))) { // 5 min grace for clock drift
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "loggedAt cannot be in the future");
        }

        ReadingSession session = ReadingSession.builder()
                .userBookId(userBook.getId())
                .durationMinutes(request.durationMinutes())
                .pagesRead(request.pagesRead())
                .loggedAt(loggedAt)
                .build();

        return toResponse(readingSessionRepository.save(session));
    }

    public List<ReadingSessionResponse> getSessions(UUID userId, UUID userBookId) {
        UserBook userBook = getOwnedUserBook(userId, userBookId);
        return readingSessionRepository.findByUserBookIdOrderByLoggedAtDesc(userBook.getId())
                .stream().map(this::toResponse).toList();
    }

    private UserBook getOwnedUserBook(UUID userId, UUID userBookId) {
        UserBook userBook = userBookRepository.findById(userBookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));
        if (!userBook.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        return userBook;
    }

    private ReadingSessionResponse toResponse(ReadingSession s) {
        return new ReadingSessionResponse(s.getId(), s.getUserBookId(), s.getDurationMinutes(), s.getPagesRead(), s.getLoggedAt());
    }
}