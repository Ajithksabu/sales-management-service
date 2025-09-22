package com.project.salesmanagement.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDTO(
                @NotBlank String name,
                @Size(max = 1000) String description,
                @NotNull @DecimalMin("0.0") BigDecimal price,
                @NotNull @Min(0) Integer quantity) {
}