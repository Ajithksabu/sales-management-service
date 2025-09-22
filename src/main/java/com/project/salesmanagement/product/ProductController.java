package com.project.salesmanagement.product;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.salesmanagement.sale.Sale;
import com.project.salesmanagement.sale.SaleDTO;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class ProductController {

    private final ProductService service;

    // pagination: /api/products?page=0&size=10&sort=name,asc
    @GetMapping("/products")
    public Page<Product> getAllProducts(Pageable pageable) {
        return service.getAllProducts(pageable);
    }

    @GetMapping("/products/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products")
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDTO dto) {
        Product created = service.addProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/products/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return service.updateProduct(id, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteProduct(id);
    }

    // sales
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sales")
    public ResponseEntity<Sale> addSale(@Valid @RequestBody SaleDTO dto) {
        Sale sale = Sale.builder()
                .product(Product.builder().id(dto.productId()).build())
                .quantity(dto.quantity())
                .salePrice(dto.salePrice())
                .saleDate(dto.saleDate())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addSale(sale));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/sales/{saleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSale(@PathVariable Long saleId) {
        service.deleteSale(saleId);
    }

    // revenue
    @GetMapping("/revenue/total")
    public BigDecimal totalRevenue() {
        return service.getTotalRevenue();
    }

    @GetMapping("/revenue/products/{productId}")
    public BigDecimal revenueByProduct(@PathVariable Long productId) {
        return service.getRevenueByProduct(productId);
    }
}
