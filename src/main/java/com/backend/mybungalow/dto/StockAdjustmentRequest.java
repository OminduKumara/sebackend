package com.backend.mybungalow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StockAdjustmentRequest {
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "IN|OUT", message = "Type must be either 'IN' or 'OUT'")
    private String type;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 255, message = "Reason must be less than 255 characters")
    private String reason;
}
