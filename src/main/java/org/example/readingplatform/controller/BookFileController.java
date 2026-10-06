package org.example.readingplatform.controller;

import org.example.readingplatform.entity.BookFile;
import org.example.readingplatform.entity.User;
import org.example.readingplatform.repository.BookFileRepository;
import org.example.readingplatform.security.AuthenticatedUserResolver;
import org.example.readingplatform.service.BookFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookFileController {

    private final BookFileService bookFileService;
    private final BookFileRepository bookFileRepository;
    private final AuthenticatedUserResolver authenticatedUserResolver;

    @PostMapping(value = "/{bookId}/files", consumes = "multipart/form-data")
    public ResponseEntity<BookFile> uploadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID bookId,
            @RequestParam("file") MultipartFile file) {
        User user = authenticatedUserResolver.resolve(userDetails);
        return ResponseEntity.ok(bookFileService.upload(bookId, user.getId(), file));
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID fileId) {
        User user = authenticatedUserResolver.resolve(userDetails);

        BookFile bookFile = bookFileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        var stream = bookFileService.download(fileId, user.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + bookFile.getOriginalFilename() + "\"")
                .body(new InputStreamResource(stream));
    }
}