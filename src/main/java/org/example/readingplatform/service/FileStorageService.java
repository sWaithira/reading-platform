package org.example.readingplatform.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {

    // Stores the file and returns a storage key/path to save on the BookFile record.
    // Not a public URL - deliberately opaque, since visibility is controlled by our own API, not storage ACLs.
    String store(MultipartFile file, String ownerUserId);

    InputStream retrieve(String storageKey);

    void delete(String storageKey);
}