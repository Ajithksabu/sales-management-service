package com.project.salesmanagement.sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SaleDTO(
                @NotNull Long productId,
                @NotNull @Min(1) Integer quantity,
                @NotNull @DecimalMin("0.0") BigDecimal salePrice,
                @NotNull LocalDateTime saleDate) {
}
