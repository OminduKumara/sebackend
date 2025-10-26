package com.backend.mybungalow.service;

import com.backend.mybungalow.domain.Category;
import com.backend.mybungalow.domain.Item;
import com.backend.mybungalow.dto.ItemCreateRequest;
import com.backend.mybungalow.dto.ItemResponse;
import com.backend.mybungalow.dto.ItemUpdateRequest;
import com.backend.mybungalow.dto.StockAdjustmentRequest;
import com.backend.mybungalow.repository.CategoryRepository;
import com.backend.mybungalow.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
    }

    public ItemResponse create(ItemCreateRequest request) {
        if (itemRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Item already exists: " + request.getName());
        }
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + request.getCategoryId()));
        
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCategory(category);
        item.setQuantityOnHand(request.getQuantityOnHand());
        item.setUnit(request.getUnit());
        
        Item saved = itemRepository.save(item);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> list() {
        return itemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponse get(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        return toResponse(item);
    }

    public ItemResponse update(Long id, ItemUpdateRequest request) {
        Item existing = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        
        if (!existing.getName().equalsIgnoreCase(request.getName()) &&
                itemRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Item already exists: " + request.getName());
        }
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + request.getCategoryId()));
        
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(category);
        existing.setQuantityOnHand(request.getQuantityOnHand());
        existing.setUnit(request.getUnit());
        
        Item saved = itemRepository.save(existing);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException("Item not found: " + id);
        }
        itemRepository.deleteById(id);
    }

    public ItemResponse adjustStock(Long id, StockAdjustmentRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));

        int currentQuantity = item.getQuantityOnHand();
        int adjustmentQuantity = request.getQuantity();

        if ("IN".equals(request.getType())) {
            item.setQuantityOnHand(currentQuantity + adjustmentQuantity);
        } else if ("OUT".equals(request.getType())) {
            if (currentQuantity < adjustmentQuantity) {
                throw new BadRequestException("Insufficient stock. Current: " + currentQuantity + ", Requested: " + adjustmentQuantity);
            }
            item.setQuantityOnHand(currentQuantity - adjustmentQuantity);
        }

        Item saved = itemRepository.save(item);


        if (saved.getQuantityOnHand() < 5) {
            notificationService.sendLowStockAlert(saved);
        }

        return toResponse(saved);
    }

    
    private ItemResponse toResponse(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setCategoryId(item.getCategory().getId());
        response.setCategoryName(item.getCategory().getName());
        response.setQuantityOnHand(item.getQuantityOnHand());
        response.setUnit(item.getUnit());
        return response;
    }

}


