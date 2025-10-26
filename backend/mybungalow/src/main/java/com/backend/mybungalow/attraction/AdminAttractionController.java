package com.backend.mybungalow.attraction;

import com.backend.mybungalow.attraction.dto.AttractionResponse;
import com.backend.mybungalow.attraction.dto.CreateAttractionRequest;
import com.backend.mybungalow.attraction.dto.UpdateAttractionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/attractions")
public class AdminAttractionController {
    private final AttractionService attractionService;

    public AdminAttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    @GetMapping
    public ResponseEntity<List<AttractionResponse>> getAllAttractions() {
        return ResponseEntity.ok(attractionService.getAllAttractions());
    }

    @PostMapping
    public ResponseEntity<AttractionResponse> createAttraction(@Valid @RequestBody CreateAttractionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attractionService.createAttraction(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttractionResponse> updateAttraction(@PathVariable Long id, @Valid @RequestBody UpdateAttractionRequest request) {
        return ResponseEntity.ok(attractionService.updateAttraction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttraction(@PathVariable Long id) {
        attractionService.deleteAttraction(id);
        return ResponseEntity.noContent().build();
    }
}

