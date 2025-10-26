package com.backend.mybungalow.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService {
    private final Path galleryDir = Paths.get("src/main/resources/static/gallery");

    public GalleryService() throws IOException {
        if (!Files.exists(galleryDir)) {
            Files.createDirectories(galleryDir);
        }
    }

    public List<String> listFilenames() throws IOException {
        if (!Files.exists(galleryDir)) return List.of();
        return Files.list(galleryDir)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    public String save(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IOException("Empty file");
        String original = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + (original == null ? "upload" : original.replaceAll("[^a-zA-Z0-9._-]", "_"));
        Path target = galleryDir.resolve(filename);
        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return filename;
    }

    public boolean delete(String filename) throws IOException {
        if (filename == null || filename.isBlank()) return false;
        // avoid directory traversal
        String safe = filename.replace("..", "");
        Path target = galleryDir.resolve(safe).normalize();
        if (!target.startsWith(galleryDir)) return false;
        if (Files.exists(target)) {
            return Files.deleteIfExists(target);
        }
        return false;
    }
}
