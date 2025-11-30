package com.tryneweveryweek.bulkupload.service;

import com.tryneweveryweek.bulkupload.model.Product;
import com.tryneweveryweek.bulkupload.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    /**
     * Bulk upload convenience used by ProductController.upload()
     * (For the metadata-enabled bulk you already have BulkProductController.)
     */
    public List<Product> bulkUpload(List<MultipartFile> files) throws IOException {
        // simple version: create a product per file, store a file and product.json
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            Product product = new Product();
            product.setId(UUID.randomUUID().toString());
            product.setName(removeExtension(file.getOriginalFilename()));
            product.setSku("SKU-" + System.currentTimeMillis());
            product.setDescription("Auto-generated");
            product.setPrice(null);

            // save to DB to get persisted entity (and ensure id present)
            Product saved = productRepository.save(product);

            // store file and product.json (FileStorageService will write product.json using saved)
            String savedFileName = fileStorageService.storeImage(saved, file);

            // update product with filename and save again
            saved.setImageFileName(savedFileName);
            productRepository.save(saved);
        }
        return productRepository.findAll();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product create(Product p) {
        if (p.getId() == null) p.setId(UUID.randomUUID().toString());
        return productRepository.save(p);
    }

    public Product update(String id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setSku(updated.getSku());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public void delete(String id) {
        productRepository.deleteById(id);
        // optionally: delete files on disk (not implemented here)
    }

    public java.util.Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    private String removeExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        return (idx > 0) ? filename.substring(0, idx) : filename;
    }
}
