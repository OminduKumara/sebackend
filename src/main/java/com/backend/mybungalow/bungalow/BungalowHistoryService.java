package com.backend.mybungalow.bungalow;

import com.backend.mybungalow.bungalow.dto.BungalowHistoryResponse;
import com.backend.mybungalow.bungalow.dto.CreateHistoryRequest;
import com.backend.mybungalow.bungalow.dto.UpdateHistoryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BungalowHistoryService {
    private final BungalowHistoryRepository bungalowHistoryRepository;

    public BungalowHistoryService(BungalowHistoryRepository bungalowHistoryRepository) {
        this.bungalowHistoryRepository = bungalowHistoryRepository;
    }

    public List<BungalowHistoryResponse> getAllHistory() {
        return bungalowHistoryRepository.findAllByOrderByYearDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BungalowHistoryResponse> getHistoryByCategory(String category) {
        return bungalowHistoryRepository.findByCategoryOrderByYearDesc(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BungalowHistoryResponse createHistory(CreateHistoryRequest request) {
        BungalowHistory history = new BungalowHistory();
        history.setTitle(request.title());
        history.setContent(request.content());
        history.setCategory(request.category());
        history.setYear(request.year());
        history.setImageUrl(request.imageUrl());
        
        BungalowHistory saved = bungalowHistoryRepository.save(history);
        return mapToResponse(saved);
    }

    @Transactional
    public BungalowHistoryResponse updateHistory(Long id, UpdateHistoryRequest request) {
        BungalowHistory history = bungalowHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("History entry not found"));
        
        history.setTitle(request.title());
        history.setContent(request.content());
        history.setCategory(request.category());
        history.setYear(request.year());
        history.setImageUrl(request.imageUrl());
        
        BungalowHistory saved = bungalowHistoryRepository.save(history);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteHistory(Long id) {
        if (!bungalowHistoryRepository.existsById(id)) {
            throw new RuntimeException("History entry not found");
        }
        bungalowHistoryRepository.deleteById(id);
    }

    public BungalowHistoryResponse getHistoryById(Long id) {
        BungalowHistory history = bungalowHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("History entry not found"));
        return mapToResponse(history);
    }

    private BungalowHistoryResponse mapToResponse(BungalowHistory history) {
        return new BungalowHistoryResponse(
                history.getId(),
                history.getTitle(),
                history.getContent(),
                history.getCategory(),
                history.getYear(),
                history.getImageUrl(),
                history.getCreatedAt()
        );
    }
}
