package org.example.ecommerce.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ecommerce.Entity.User;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.UserRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/images")
public class UserImageController {

    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER','SELLER')")
    @GetMapping("/{userId}")
    public ResponseEntity<Resource> getUserProfileImage(@PathVariable Long userId, Authentication authentication) {
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email);
        if (requester == null) {
            throw new APIException("User not found", HttpStatus.UNAUTHORIZED);
        }

        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(r -> "ADMIN".equals(r.getAuthority().name()));

        if (!isAdmin && !requester.getId().equals(userId)) {
            throw new APIException("Access denied for this image", HttpStatus.FORBIDDEN);
        }

        List<String> exts = List.of(".jpg", ".jpeg", ".png", ".webp", ".bmp");
        Path basePath = Paths.get(System.getProperty("user.dir"), "images", "user");

        File matched = null;
        for (String ext : exts) {
            File f = basePath.resolve(userId + ext).toFile();
            if (f.exists() && f.isFile()) {
                matched = f;
                break;
            }
        }

        if (matched == null) {
            throw new APIException("Image not found", HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(matched);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        String name = matched.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
        else if (name.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
        else if (name.endsWith(".webp")) mediaType = MediaType.parseMediaType("image/webp");
        else if (name.endsWith(".bmp")) mediaType = MediaType.parseMediaType("image/bmp");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(resource);
    }
}