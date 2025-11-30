package com.tryneweveryweek.bulkupload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryneweveryweek.bulkupload.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;

@Service
public class FileStorageService {

    private final Path root;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileStorageService(@Value("${product.storage.base-dir:./uploads}") String baseDir) throws IOException {
        this.root = Paths.get(baseDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    /**
     * Store image into: {baseDir}/{productId}/{originalFilename}
     * Also writes product.json inside that folder with basic product fields + savedAt.
     *
     * Returns the saved filename (original filename sanitized).
     */
    public String storeImage(Product product, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        String original = StringUtils.cleanPath(image.getOriginalFilename());
        if (original.contains("..")) {
            throw new IllegalArgumentException("Invalid path in filename: " + original);
        }

        // create product folder
        Path productDir = root.resolve(product.getId());
        Files.createDirectories(productDir);

        Path imagePath = productDir.resolve(original);
        // Save to disk (use copy to avoid Tomcat temp-file issues)
        try {
            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to save file " + original + " -> " + e.getMessage(), e);
        }

        // write product.json (metadata) in the same folder
        File jsonFile = productDir.resolve("product.json").toFile();
        // Create a small metadata structure to persist (avoid full entity recursion)
        var meta = new java.util.HashMap<String, Object>();
        meta.put("id", product.getId());
        meta.put("name", product.getName());
        meta.put("sku", product.getSku());
        meta.put("description", product.getDescription());
        meta.put("price", product.getPrice());
        meta.put("imageFileName", original);
        meta.put("savedAt", Instant.now().toString());

        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, meta);

        return original;
    }

    /**
     * Load a stored image as a Resource for the ImageController.
     * Returns null if not found.
     */
    public Resource loadAsResource(String productId, String filename) {
        try {
            Path file = root.resolve(productId).resolve(filename).normalize();
            if (!Files.exists(file) || !Files.isRegularFile(file)) return null;
            return new UrlResource(file.toUri());
        } catch (Exception ex) {
            return null;
        }
    }

    // alias name used in some controllers: keep both
    public Resource loadImage(String productId, String filename) {
        return loadAsResource(productId, filename);
    }
}
