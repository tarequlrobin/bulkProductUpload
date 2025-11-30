package com.tryneweveryweek.bulkupload.controller;

import com.tryneweveryweek.bulkupload.dto.ProductResponseDto;
import com.tryneweveryweek.bulkupload.model.Product;
import com.tryneweveryweek.bulkupload.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> list() {
        List<Product> products = service.getAllProducts();

        List<ProductResponseDto> response = products.stream().map(prod -> {
            String url = null;
            if (prod.getImageFileName() != null) {
                url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(prod.getId() + "/")
                        .path(prod.getImageFileName())
                        .toUriString();
            }
            return new ProductResponseDto(prod, url);
        }).toList();

        return ResponseEntity.ok(response);
    }
}
