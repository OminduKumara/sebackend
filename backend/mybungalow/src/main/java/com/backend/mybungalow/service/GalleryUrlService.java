package com.backend.mybungalow.service;

import com.backend.mybungalow.model.GalleryItem;
import com.backend.mybungalow.repository.GalleryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryUrlService {
    private final GalleryItemRepository repo;

    public GalleryUrlService(GalleryItemRepository repo) {
        this.repo = repo;
    }

    public GalleryItem add(String url) {
        var item = GalleryItem.builder().imageUrl(url).build();
        return repo.save(item);
    }

    public List<GalleryItem> listAll() {
        return repo.findAll();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
