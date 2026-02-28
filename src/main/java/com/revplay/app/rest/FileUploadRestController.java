package com.revplay.app.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadRestController {

    private static final Logger logger = LogManager.getLogger(FileUploadRestController.class);
    private static final String UPLOAD_DIR = "uploads/audio";
    private static final String IMAGE_UPLOAD_DIR = "uploads/images";

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No file selected"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Only image files are allowed"));
        }
        try {
            Path uploadPath = Paths.get(IMAGE_UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/images/" + uniqueFilename;
            logger.info("Uploaded image file: {} -> {}", originalFilename, fileUrl);
            return ResponseEntity.ok(Map.of("success", true, "fileUrl", fileUrl,
                    "originalName", originalFilename != null ? originalFilename : uniqueFilename));
        } catch (IOException e) {
            logger.error("Failed to upload image: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Upload failed"));
        }
    }

    @PostMapping("/audio")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No file selected"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Only audio files are allowed"));
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileUrl = "/audio/" + uniqueFilename;
            logger.info("Uploaded audio file: {} -> {}", originalFilename, fileUrl);
            return ResponseEntity.ok(Map.of("success", true, "fileUrl", fileUrl,
                    "originalName", originalFilename != null ? originalFilename : uniqueFilename));
        } catch (IOException e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Upload failed"));
        }
    }
}
