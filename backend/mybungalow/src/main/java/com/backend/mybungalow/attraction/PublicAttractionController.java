package com.backend.mybungalow.attraction;

import com.backend.mybungalow.attraction.dto.AttractionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attractions")
public class PublicAttractionController {
    private final AttractionService attractionService;

    public PublicAttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public ResponseEntity<List<AttractionResponse>> getAttractions() {
        return ResponseEntity.ok(attractionService.getPublicAttractions());
    }
}

