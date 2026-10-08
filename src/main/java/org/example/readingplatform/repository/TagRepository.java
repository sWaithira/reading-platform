package org.example.readingplatform.repository;

import org.example.readingplatform.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    List<Tag> findByType(Tag.TagType type);
    Optional<Tag> findByNameIgnoreCaseAndType(String name, Tag.TagType type);
}