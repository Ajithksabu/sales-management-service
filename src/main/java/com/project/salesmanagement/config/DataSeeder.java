package com.project.salesmanagement.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.salesmanagement.product.Product;
import com.project.salesmanagement.product.ProductRepository;
import com.project.salesmanagement.sale.Sale;
import com.project.salesmanagement.sale.SaleRepository;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(ProductRepository pr, SaleRepository sr) {
        return args -> {
            if (pr.count() == 0) {
                Product p1 = pr.save(Product.builder().name("Laptop").description("15-inch")
                        .price(new BigDecimal("900.00")).quantity(20).build());
                Product p2 = pr.save(Product.builder().name("Mouse").description("Wireless")
                        .price(new BigDecimal("25.00")).quantity(100).build());
                sr.save(Sale.builder().product(p1).quantity(2).salePrice(new BigDecimal("900.00"))
                        .saleDate(LocalDateTime.now().minusDays(1)).build());
                sr.save(Sale.builder().product(p2).quantity(10).salePrice(new BigDecimal("25.00"))
                        .saleDate(LocalDateTime.now()).build());
                p1.setQuantity(18);
                p2.setQuantity(90);
                pr.save(p1);
                pr.save(p2);
            }
        };
    }
}
