package com.backend.mybungalow.controller;

import com.backend.mybungalow.model.GalleryItem;
import com.backend.mybungalow.service.GalleryService;
import com.backend.mybungalow.service.GalleryUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private GalleryUrlService galleryUrlService;

    @GetMapping("/gallery")
    public ResponseEntity<List<String>> getGallery() throws IOException {
        var list = galleryService.listFilenames();
        // Static file URLs
        var urls = list.stream().map(name -> "/gallery/" + name).collect(Collectors.toList());
        // Add URL-based items from DB
        var dbUrls = galleryUrlService.listAll().stream().map(GalleryItem::getImageUrl).toList();
        urls.addAll(dbUrls);
        return ResponseEntity.ok(urls);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/gallery")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String filename = galleryService.save(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("/gallery/" + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save file: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/gallery/{filename}")
    public ResponseEntity<?> delete(@PathVariable String filename) {
        try {
            boolean deleted = galleryService.delete(filename);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file: " + e.getMessage());
        }
    }

    // URL-based admin endpoints
    public record AddGalleryRequest(String url) {}

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/gallery")
    public ResponseEntity<List<GalleryItem>> getAdminGallery() {
        return ResponseEntity.ok(galleryUrlService.listAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/gallery/url")
    public ResponseEntity<GalleryItem> addByUrl(@org.springframework.web.bind.annotation.RequestBody AddGalleryRequest req) {
        if (req == null || req.url() == null || req.url().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        var saved = galleryUrlService.add(req.url().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/gallery/id/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        galleryUrlService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
