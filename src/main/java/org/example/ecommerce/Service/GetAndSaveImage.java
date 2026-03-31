//package org.example.ecommerce.Service;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GetAndSaveImage {
//
//    public static String resolveImageUrl(Long userId) {
//        List<String> exts = List.of(".jpg", ".jpeg", ".png", ".webp");
//        String basePath = System.getProperty("user.dir") + "/images/user/";
//
//        for (String ext : exts) {
//            File file = new File(basePath + userId + ext);
//            if (file.exists()) {
//                return "http://localhost:8080/api/user/images/" + userId;
//            }
//        }
//
//        return null;
//    }
//    public static void uploadProductImages(String relativeUrl, MultipartFile image, String id) {
//        if (image != null && !image.isEmpty()) {
//            String originalFilename = image.getOriginalFilename();
//            String extension = "";
//
//            if (originalFilename != null && originalFilename.contains(".")) {
//                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            }
//
//            List<String> allowedExtensions = List.of(".jpg", ".jpeg", ".png", ".bmp");
//            if (!allowedExtensions.contains(extension.toLowerCase())) {
//                throw new IllegalArgumentException("Invalid file type. Only JPG, JPEG, PNG, and BMP are allowed.");
//            }
//
//            Path fullDirectoryPath = Paths.get(System.getProperty("user.dir"), relativeUrl)
//                    .toAbsolutePath()
//                    .normalize();
//
//            try {
//                Files.createDirectories(fullDirectoryPath);
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to create directory: " + fullDirectoryPath, e);
//            }
//
//            Path imagePath = fullDirectoryPath.resolve(id + extension);
//
//            try {
//                image.transferTo(imagePath.toFile());
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to save image at: " + imagePath, e);
//            }
//        } else {
//            throw new IllegalArgumentException("Image file is empty or null.");
//        }
//    }
//
//
//
//    public static String resolveProductPrimaryImage(String path , String userId) {
//        List<String> exts = List.of(".jpg", ".jpeg", ".png", ".webp");
//        String basePath = System.getProperty("user.dir") + path;
//
//        for (String ext : exts) {
//            File file = new File(basePath + userId + ext);
//            if (file.exists()) {
//                return "http://localhost:8080"+path + userId + ext;
//            }
//        }
//
//        return null;
//    }
//
//
//    public static List<String> getAllSecondaryImages(String relativePath, String userId) {
//        List<String> allowedExts = List.of(".jpg", ".jpeg", ".png", ".webp");
//        List<String> matchedImages = new ArrayList<>();
//
//        Path dirPath = Paths.get(System.getProperty("user.dir"), relativePath)
//                .toAbsolutePath()
//                .normalize();
//
//        File folder = dirPath.toFile();
//
//        if (folder.exists() && folder.isDirectory()) {
//            File[] files = folder.listFiles();
//
//            if (files != null) {
//                for (File file : files) {
//                    String name = file.getName().toLowerCase();
//
//                    if (name.startsWith(userId + "_") && allowedExts.stream().anyMatch(name::endsWith)) {
//                        matchedImages.add("http://localhost:8080" + relativePath + file.getName());
//                    }
//                }
//            }
//        }
//
//        return matchedImages;
//    }
//
//}





package org.example.ecommerce.Service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.Config.S3StorageProperties;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetAndSaveImage {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".bmp", ".webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3StorageProperties properties;

    public String resolveImageUrl(Long userId) {
        return findSingleUrlByPrefix("users/" + userId + "/profile");
    }

    public void uploadUserImage(Long userId, MultipartFile image) {
        deleteByPrefix("users/" + userId + "/profile");
        upload("users/" + userId + "/profile", image);
    }

    public void uploadProductImages(String folder, MultipartFile image, String id) {
        String normalized = folder.contains("primary")
                ? "products/primary/" + id
                : "products/secondary/" + id;
        upload(normalized, image);
    }

    public String resolveProductPrimaryImage(String ignored, String imageName) {
        return findSingleUrlByPrefix("products/primary/" + imageName);
    }

    public List<String> getAllSecondaryImages(String ignored, String imageName) {
        return findAllUrlsByPrefix("products/secondary/" + imageName + "_");
    }

    private void upload(String keyPrefix, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new APIException("Image file is empty or null.", HttpStatus.BAD_REQUEST);
        }

        String extension = extractExtension(image.getOriginalFilename());
        String key = keyPrefix + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .contentType(resolveContentType(extension, image.getContentType()))
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(image.getBytes()));
        } catch (IOException e) {
            throw new APIException("Failed to upload image to S3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String findSingleUrlByPrefix(String prefix) {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder()
                        .bucket(properties.getBucket())
                        .prefix(prefix)
                        .maxKeys(1)
                        .build())
                .contents()
                .stream()
                .findFirst()
                .map(object -> presignedUrl(object.key()))
                .orElse(null);
    }
    private List<String> findAllUrlsByPrefix(String prefix) {
        return s3Client.listObjectsV2(ListObjectsV2Request.builder()
                        .bucket(properties.getBucket())
                        .prefix(prefix)
                        .build())
                .contents()
                .stream()
                .sorted(Comparator.comparing(obj -> obj.lastModified(), Comparator.reverseOrder()))
                .map(object -> presignedUrl(object.key()))
                .toList();
    }

    private void deleteByPrefix(String prefix) {
        s3Client.listObjectsV2(ListObjectsV2Request.builder()
                        .bucket(properties.getBucket())
                        .prefix(prefix)
                        .build())
                .contents()
                .forEach(object -> s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(object.key())
                        .build()));
    }
    private String presignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(properties.getUrlTtl())
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new APIException("File extension is missing", HttpStatus.BAD_REQUEST);
        }

        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new APIException("Only JPG, JPEG, PNG, BMP and WEBP are allowed.", HttpStatus.BAD_REQUEST);
        }
        return extension;
    }
    private String resolveContentType(String extension, String fallback) {
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".bmp" -> "image/bmp";
            case ".webp" -> "image/webp";
            default -> fallback != null ? fallback : "application/octet-stream";
        };
    }
}