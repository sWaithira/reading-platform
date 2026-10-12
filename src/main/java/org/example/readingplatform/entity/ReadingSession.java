package org.example.readingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reading_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingSession {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userBookId;

    private Integer durationMinutes; // at least one of duration/pages is required (enforced in the service)

    private Integer pagesRead;

    @Column(nullable = false)
    private Instant loggedAt;

    @PrePersist
    void onCreate() {
        if (this.loggedAt == null) {
            this.loggedAt = Instant.now();
        }
    }
}