package com.backend.mybungalow.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateRoomDto {
    @Size(max = 150)
    private String name;

    @Size(max = 2000)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Min(1)
    private Integer capacity;

    private String status;
}
