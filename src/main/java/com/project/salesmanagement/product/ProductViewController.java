package com.project.salesmanagement.product;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductViewController {
    private final ProductService productService;

    @GetMapping("/products/view")
    public String view(Model model) {
        model.addAttribute("products", productService.getAllProducts(Pageable.unpaged()).getContent());
        return "products";
    }
}
