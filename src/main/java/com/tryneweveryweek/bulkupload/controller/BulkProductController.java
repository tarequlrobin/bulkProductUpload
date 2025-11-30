package com.tryneweveryweek.bulkupload.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryneweveryweek.bulkupload.model.Product;
import com.tryneweveryweek.bulkupload.repository.ProductRepository;
import com.tryneweveryweek.bulkupload.service.FileStorageService;
import com.tryneweveryweek.bulkupload.dto.ProductResponseDto;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class BulkProductController {

    private final ProductRepository repo;
    private final FileStorageService storage;
    private final ObjectMapper mapper = new ObjectMapper();

    public BulkProductController(ProductRepository repo, FileStorageService storage) {
        this.repo = repo;
        this.storage = storage;
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBulk(
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("metadatas") String metadataJson
    ) {
        try {

            // Parse metadata: array -> filename -> meta
            List<Map<String, Object>> arr = mapper.readValue(
                    metadataJson, new TypeReference<>() {});
            Map<String, Map<String, Object>> metaMap = new HashMap<>();

            for (Map<String, Object> item : arr) {
                String filename = Objects.toString(item.get("filename"), null);
                Map<String, Object> meta = (Map<String, Object>) item.get("meta");
                if (filename != null && meta != null) {
                    metaMap.put(filename, meta);
                }
            }

            List<ProductResponseDto> responses = new ArrayList<>();

            for (MultipartFile file : files) {

                String originalName = file.getOriginalFilename();
                Map<String, Object> meta = metaMap.get(originalName);

                Product product = new Product();
                product.setName(meta != null ? meta.get("name").toString() : originalName);
                product.setSku(meta != null ? Objects.toString(meta.get("sku"), null) : null);
                product.setDescription(meta != null ? Objects.toString(meta.get("description"), null) : null);

                if (meta != null && meta.get("price") != null) {
                    try {
                        product.setPrice(Double.valueOf(meta.get("price").toString()));
                    } catch (Exception ignored) {}
                }

                // Save to DB to generate UUID
                Product created = repo.save(product);

                // Save to disk
                String savedFile = storage.storeImage(created, file);
                created.setImageFileName(savedFile);

                repo.save(created);

                String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(created.getId() + "/")
                        .path(savedFile)
                        .toUriString();

                responses.add(new ProductResponseDto(created, imageUrl));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(responses);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Upload failed: " + ex.getMessage());
        }
    }
}
