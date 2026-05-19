package com.beautysalon.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/inventory/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
/**
 * OOP Concepts Applied:
 * - Encapsulation: Exposes REST endpoints while hiding internal business logic.
 * - Abstraction: Interacts with Service interfaces/classes without knowing their implementation details.
 * - Single Responsibility Principle (SOLID): Responsible solely for handling HTTP requests and responses.
 */
public class FileUploadController {

    // Target upload folder in the frontend static folder for easy web rendering
    private static final String UPLOAD_DIR_PATH = "c:\\Users\\Chithu\\Music\\BeautySalon\\BeautySalon\\frontend\\uploads";

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadProductImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "Please select a valid image file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Create uploads directory if it does not exist
            File directory = new File(UPLOAD_DIR_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique filename to avoid duplicates
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + extension;

            // Save the file to the target path
            Path targetPath = Paths.get(UPLOAD_DIR_PATH, newFileName);
            Files.write(targetPath, file.getBytes());

            // Return relative path for frontend rendering
            response.put("imageUrl", "uploads/" + newFileName);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("error", "Failed to save file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
