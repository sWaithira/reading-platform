package org.example.readingplatform.controller;

import org.example.readingplatform.dto.CreateTagRequest;
import org.example.readingplatform.dto.TagResponse;
import org.example.readingplatform.entity.Tag;
import org.example.readingplatform.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> listTags(@RequestParam(required = false) Tag.TagType type) {
        return ResponseEntity.ok(tagService.listTags(type));
    }

    @PostMapping("/tags")
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
        return ResponseEntity.ok(tagService.createTag(request));
    }

    @GetMapping("/books/{bookId}/tags")
    public ResponseEntity<List<TagResponse>> getBookTags(@PathVariable UUID bookId) {
        return ResponseEntity.ok(tagService.getTagsForBook(bookId));
    }

    @PostMapping("/books/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToBook(@PathVariable UUID bookId, @PathVariable UUID tagId) {
        tagService.addTagToBook(bookId, tagId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/books/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromBook(@PathVariable UUID bookId, @PathVariable UUID tagId) {
        tagService.removeTagFromBook(bookId, tagId);
        return ResponseEntity.noContent().build();
    }
}