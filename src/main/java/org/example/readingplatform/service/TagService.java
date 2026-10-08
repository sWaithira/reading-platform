package org.example.readingplatform.service;

import org.example.readingplatform.dto.CreateTagRequest;
import org.example.readingplatform.dto.TagResponse;
import org.example.readingplatform.entity.BookTag;
import org.example.readingplatform.entity.Tag;
import org.example.readingplatform.repository.BookTagRepository;
import org.example.readingplatform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    public List<TagResponse> listTags(Tag.TagType type) {
        List<Tag> tags = type != null ? tagRepository.findByType(type) : tagRepository.findAll();
        return tags.stream().map(this::toResponse).toList();
    }

    // User-created tags reuse an existing tag with the same name+type instead of
    // duplicating it - keeps "romance" as one row, not one per user who typed it.
    public TagResponse createTag(CreateTagRequest request) {
        Tag tag = tagRepository.findByNameIgnoreCaseAndType(request.name(), request.type())
                .orElseGet(() -> tagRepository.save(
                        Tag.builder()
                                .name(request.name().toLowerCase())
                                .type(request.type())
                                .isSystem(false)
                                .build()
                ));
        return toResponse(tag);
    }

    public List<TagResponse> getTagsForBook(UUID bookId) {
        return bookTagRepository.findByBookId(bookId).stream()
                .map(bt -> tagRepository.findById(bt.getTagId()).orElseThrow())
                .map(this::toResponse)
                .toList();
    }

    public void addTagToBook(UUID bookId, UUID tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found");
        }
        if (bookTagRepository.findByBookIdAndTagId(bookId, tagId).isPresent()) {
            return; // already tagged - treat as idempotent rather than erroring
        }
        bookTagRepository.save(BookTag.builder().bookId(bookId).tagId(tagId).build());
    }

    public void removeTagFromBook(UUID bookId, UUID tagId) {
        BookTag bookTag = bookTagRepository.findByBookIdAndTagId(bookId, tagId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book is not tagged with this tag"));
        bookTagRepository.delete(bookTag);
    }

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getType(), tag.isSystem());
    }
}