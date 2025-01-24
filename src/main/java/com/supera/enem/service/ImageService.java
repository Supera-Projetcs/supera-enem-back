package com.supera.enem.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageService {

    private final String uploadDir = "upload/images";

    public String saveImage(MultipartFile image) throws IOException {

        if (!image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file format");
        }

        if (image.isEmpty()) {
            throw new IOException("The file is empty");
        }

        Path uploadPath = Path.of(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        image.transferTo(filePath.toFile());

        System.out.println("Caminho gerado: " + filePath.toAbsolutePath().toString());

        return filePath.toAbsolutePath().toString();
    }
}

