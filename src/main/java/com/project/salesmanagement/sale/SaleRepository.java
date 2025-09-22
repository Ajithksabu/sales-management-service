package com.project.salesmanagement.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface SaleRepository extends JpaRepository<Sale, Long> {

  @Query("select coalesce(sum(s.salePrice * s.quantity), 0) from Sale s")
  BigDecimal totalRevenue();

  @Query("select coalesce(sum(s.salePrice * s.quantity), 0) from Sale s where s.product.id = :productId")
  BigDecimal revenueByProduct(Long productId);
}
