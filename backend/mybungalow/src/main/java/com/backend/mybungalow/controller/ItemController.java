package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.ItemCreateRequest;
import com.backend.mybungalow.dto.ItemResponse;
import com.backend.mybungalow.dto.ItemUpdateRequest;
import com.backend.mybungalow.dto.StockAdjustmentRequest;
import com.backend.mybungalow.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = itemService.list();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.get(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemCreateRequest request) {
        ItemResponse created = itemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequest request) {
        ItemResponse updated = itemService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/adjust-stock")
    public ResponseEntity<ItemResponse> adjustStock(@PathVariable Long id, @Valid @RequestBody StockAdjustmentRequest request) {
        ItemResponse updated = itemService.adjustStock(id, request);
        return ResponseEntity.ok(updated);
    }
}
