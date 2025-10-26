package com.backend.mybungalow.dto;

import lombok.Data;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Integer quantityOnHand;
    private String unit;
}
