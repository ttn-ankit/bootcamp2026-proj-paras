package org.example.ecommerce.Service;

import java.io.File;
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
}