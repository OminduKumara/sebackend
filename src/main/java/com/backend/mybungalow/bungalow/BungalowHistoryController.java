package com.backend.mybungalow.bungalow;

import com.backend.mybungalow.bungalow.dto.BungalowHistoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bungalow")
public class BungalowHistoryController {
    private final BungalowHistoryService bungalowHistoryService;

    public BungalowHistoryController(BungalowHistoryService bungalowHistoryService) {
        this.bungalowHistoryService = bungalowHistoryService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<BungalowHistoryResponse>> getBungalowHistory(
            @RequestParam(required = false) String category
    ) {
        List<BungalowHistoryResponse> history;
        if (category != null && !category.trim().isEmpty()) {
            history = bungalowHistoryService.getHistoryByCategory(category);
        } else {
            history = bungalowHistoryService.getAllHistory();
        }
        return ResponseEntity.ok(history);
    }
}
