package com.backend.mybungalow.bungalow;

import com.backend.mybungalow.bungalow.dto.BungalowHistoryResponse;
import com.backend.mybungalow.bungalow.dto.CreateHistoryRequest;
import com.backend.mybungalow.bungalow.dto.UpdateHistoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bungalow")
public class AdminBungalowHistoryController {
    private final BungalowHistoryService bungalowHistoryService;

    public AdminBungalowHistoryController(BungalowHistoryService bungalowHistoryService) {
        this.bungalowHistoryService = bungalowHistoryService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<BungalowHistoryResponse>> getAllHistory() {
        List<BungalowHistoryResponse> history = bungalowHistoryService.getAllHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<BungalowHistoryResponse> getHistoryById(@PathVariable Long id) {
        BungalowHistoryResponse history = bungalowHistoryService.getHistoryById(id);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/history")
    public ResponseEntity<BungalowHistoryResponse> createHistory(@Valid @RequestBody CreateHistoryRequest request) {
        BungalowHistoryResponse history = bungalowHistoryService.createHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(history);
    }

    @PutMapping("/history/{id}")
    public ResponseEntity<BungalowHistoryResponse> updateHistory(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateHistoryRequest request
    ) {
        BungalowHistoryResponse history = bungalowHistoryService.updateHistory(id, request);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        bungalowHistoryService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }
}
