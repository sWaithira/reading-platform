package org.example.readingplatform.service;

import org.example.readingplatform.entity.BookFile;
import org.example.readingplatform.repository.BookFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookFileService {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024; // 50MB

    private final FileStorageService fileStorageService;
    private final BookFileRepository bookFileRepository;

    public BookFile upload(UUID bookId, UUID userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds 50MB limit");
        }

        BookFile.FileType fileType = resolveFileType(file.getOriginalFilename(), file.getContentType());
        String storageKey = fileStorageService.store(file, userId.toString());

        BookFile bookFile = BookFile.builder()
                .bookId(bookId)
                .userId(userId)
                .fileType(fileType)
                .storageKey(storageKey)
                .originalFilename(file.getOriginalFilename())
                .fileSizeBytes(file.getSize())
                .build();

        return bookFileRepository.save(bookFile);
    }

    public InputStream download(UUID fileId, UUID requestingUserId) {
        BookFile bookFile = bookFileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        if (!bookFile.getUserId().equals(requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"); // 404, not 403 - same pattern as library ownership
        }

        return fileStorageService.retrieve(bookFile.getStorageKey());
    }

    private BookFile.FileType resolveFileType(String filename, String contentType) {
        if (filename == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing filename");
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) return BookFile.FileType.PDF;
        if (lower.endsWith(".epub")) return BookFile.FileType.EPUB;
        if (lower.endsWith(".txt") || lower.endsWith(".html") || lower.endsWith(".md")) return BookFile.FileType.ARTICLE;

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Unsupported file type - only PDF, EPUB, TXT, HTML, or MD accepted");
    }
}