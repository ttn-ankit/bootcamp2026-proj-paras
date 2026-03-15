package org.example.ecommerce.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GetAndSaveImage {

    public static String resolveImageUrl(Long userId) {
        List<String> exts = List.of(".jpg", ".jpeg", ".png", ".webp");
        String basePath = System.getProperty("user.dir") + "/images/user/";

        for (String ext : exts) {
            File file = new File(basePath + userId + ext);
            if (file.exists()) {
                return "http://localhost:8080/images/user/" + userId + ext;
            }
        }

        return null;
    }
    public static void uploadProductImages(String relativeUrl, MultipartFile image, String id) {
        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".bmp");
            if (!allowedExtensions.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("Invalid file type. Only JPG, JPEG, PNG, and BMP are allowed.");
            }

            Path fullDirectoryPath = Paths.get(System.getProperty("user.dir"), relativeUrl)
                    .toAbsolutePath()
                    .normalize();

            try {
                Files.createDirectories(fullDirectoryPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + fullDirectoryPath, e);
            }

            Path imagePath = fullDirectoryPath.resolve(id + extension);

            try {
                image.transferTo(imagePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image at: " + imagePath, e);
            }
        } else {
            throw new IllegalArgumentException("Image file is empty or null.");
        }
    }



    public static String resolveProductPrimaryImage(String path , String userId) {
        List<String> exts = List.of(".jpg", ".jpeg", ".png", ".webp");
        String basePath = System.getProperty("user.dir") + path;

        for (String ext : exts) {
            File file = new File(basePath + userId + ext);
            if (file.exists()) {
                return "http://localhost:8080"+path + userId + ext;
            }
        }

        return null;
    }


    public static List<String> getAllSecondaryImages(String relativePath, String userId) {
        List<String> allowedExts = List.of(".jpg", ".jpeg", ".png", ".webp");
        List<String> matchedImages = new ArrayList<>();

        Path dirPath = Paths.get(System.getProperty("user.dir"), relativePath)
                .toAbsolutePath()
                .normalize();

        File folder = dirPath.toFile();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    String name = file.getName().toLowerCase();

                    if (name.startsWith(userId + "_") && allowedExts.stream().anyMatch(name::endsWith)) {
                        matchedImages.add("http://localhost:8080" + relativePath + file.getName());
                    }
                }
            }
        }

        return matchedImages;
    }

}