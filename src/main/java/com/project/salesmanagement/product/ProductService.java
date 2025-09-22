package com.project.salesmanagement.product;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.salesmanagement.exception.NotFoundException;
import com.project.salesmanagement.sale.Sale;
import com.project.salesmanagement.sale.SaleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepo;
    private final SaleRepository saleRepo;

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    @Transactional
    public Product addProduct(ProductDTO dto) {
        Product p = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .quantity(dto.quantity())
                .build();
        return productRepo.save(p);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDTO dto) {
        Product p = getProductById(id);
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setQuantity(dto.quantity());
        return productRepo.save(p);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepo.existsById(id))
            throw new NotFoundException("Product not found: " + id);
        productRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return saleRepo.totalRevenue();
    }

    @Transactional(readOnly = true)
    public BigDecimal getRevenueByProduct(Long productId) {
        // validate product exists to return 404 if not
        getProductById(productId);
        return saleRepo.revenueByProduct(productId);
    }

    @Transactional
    public Sale addSale(Sale sale) {
        // adjust inventory
        Product p = getProductById(sale.getProduct().getId());
        if (p.getQuantity() < sale.getQuantity())
            throw new IllegalArgumentException("Insufficient stock for product: " + p.getId());
        p.setQuantity(p.getQuantity() - sale.getQuantity());
        productRepo.save(p);
        return saleRepo.save(sale);
    }

    @Transactional
    public void deleteSale(Long saleId) {
        Sale s = saleRepo.findById(saleId).orElseThrow(() -> new NotFoundException("Sale not found: " + saleId));
        // restock
        Product p = s.getProduct();
        p.setQuantity(p.getQuantity() + s.getQuantity());
        productRepo.save(p);
        saleRepo.delete(s);
    }
}
