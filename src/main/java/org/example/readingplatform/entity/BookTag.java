package org.example.readingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "book_tags", uniqueConstraints = @UniqueConstraint(columnNames = {"bookId", "tagId"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookTag {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private UUID tagId;
}