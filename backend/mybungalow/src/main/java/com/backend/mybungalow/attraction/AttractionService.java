package com.backend.mybungalow.attraction;

import com.backend.mybungalow.attraction.dto.AttractionResponse;
import com.backend.mybungalow.attraction.dto.CreateAttractionRequest;
import com.backend.mybungalow.attraction.dto.UpdateAttractionRequest;
import com.backend.mybungalow.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttractionService {
    private final AttractionRepository attractionRepository;

    public AttractionService(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    public List<AttractionResponse> getPublicAttractions() {
        return attractionRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public List<AttractionResponse> getAllAttractions() {
        return attractionRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttractionResponse createAttraction(CreateAttractionRequest request) {
        Attraction attraction = new Attraction();
        attraction.setName(request.name());
        attraction.setDescription(request.description());
        attraction.setCategory(request.category());
        attraction.setLocation(request.location());
        attraction.setDistance(request.distance());
        attraction.setIsActive(request.isActive());
        attraction.setDisplayOrder(request.displayOrder());
        return map(attractionRepository.save(attraction));
    }

    @Transactional
    public AttractionResponse updateAttraction(Long id, UpdateAttractionRequest request) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found with id: " + id));

        if (request.name() != null) attraction.setName(request.name());
        if (request.description() != null) attraction.setDescription(request.description());
        if (request.category() != null) attraction.setCategory(request.category());
        if (request.location() != null) attraction.setLocation(request.location());
        if (request.distance() != null) attraction.setDistance(request.distance());
        if (request.isActive() != null) attraction.setIsActive(request.isActive());
        if (request.displayOrder() != null) attraction.setDisplayOrder(request.displayOrder());

        return map(attractionRepository.save(attraction));
    }

    @Transactional
    public void deleteAttraction(Long id) {
        if (!attractionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attraction not found with id: " + id);
        }
        attractionRepository.deleteById(id);
    }

    private AttractionResponse map(Attraction attraction) {
        return new AttractionResponse(
                attraction.getId(),
                attraction.getName(),
                attraction.getDescription(),
                attraction.getCategory(),
                attraction.getLocation(),
                attraction.getDistance(),
                attraction.getIsActive(),
                attraction.getDisplayOrder(),
                attraction.getCreatedAt()
        );
    }
}

