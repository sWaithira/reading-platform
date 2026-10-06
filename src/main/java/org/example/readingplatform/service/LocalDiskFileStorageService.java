package org.example.readingplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// Local disk storage - fine for development. NOT suitable for production on
// Railway/Render/Fly.io, since their filesystems are ephemeral and wiped on
// every redeploy. Swap for an S3StorageService (e.g. Cloudflare R2) before
// going live; nothing outside this class needs to change since it's behind
// the FileStorageService interface.
@Service
@Slf4j
public class LocalDiskFileStorageService implements FileStorageService {

    @Value("${app.storage.local-path:./uploads}")
    private String basePath;

    @Override
    public String store(MultipartFile file, String ownerUserId) {
        try {
            Path dir = Path.of(basePath, ownerUserId);
            Files.createDirectories(dir);

            String extension = getExtension(file.getOriginalFilename());
            String key = ownerUserId + "/" + UUID.randomUUID() + extension;
            Path target = Path.of(basePath, key);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return key;
        } catch (IOException e) {
            log.error("Failed to store file for user {}", ownerUserId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }
    }

    @Override
    public InputStream retrieve(String storageKey) {
        try {
            return Files.newInputStream(Path.of(basePath, storageKey));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(Path.of(basePath, storageKey));
        } catch (IOException e) {
            log.warn("Failed to delete file {}", storageKey, e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}