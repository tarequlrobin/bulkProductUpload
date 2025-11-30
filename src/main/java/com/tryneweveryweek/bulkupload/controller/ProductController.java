package com.tryneweveryweek.bulkupload.controller;

import com.tryneweveryweek.bulkupload.model.Product;
import com.tryneweveryweek.bulkupload.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product saved = service.create(product);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Product>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody Product p) {
        try {
            Product updated = service.update(id, p);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
