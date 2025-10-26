package com.backend.mybungalow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ItemUpdateRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Min(value = 0, message = "Quantity on hand must be 0 or greater")
    private Integer quantityOnHand;

    @Size(max = 50, message = "Unit must be less than 50 characters")
    private String unit;
}
