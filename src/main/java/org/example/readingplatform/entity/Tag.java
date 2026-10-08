package org.example.readingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tags", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "type"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name; // e.g. "romance", "slow-burn", "spice-level-3"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagType type;

    @Column(nullable = false)
    private boolean isSystem; // system-curated vs. user-created

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public enum TagType {
        GENRE, TROPE, MOOD, CUSTOM
    }
}