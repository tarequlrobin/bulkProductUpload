package com.tryneweveryweek.bulkupload.controller;

import com.tryneweveryweek.bulkupload.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final FileStorageService storage;

    public ImageController(FileStorageService storage) {
        this.storage = storage;
    }

    @GetMapping("/{id}/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String id,
                                               @PathVariable String filename) {
        Resource resource = storage.loadImage(id, filename);
        if (resource == null) return ResponseEntity.notFound().build();
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
