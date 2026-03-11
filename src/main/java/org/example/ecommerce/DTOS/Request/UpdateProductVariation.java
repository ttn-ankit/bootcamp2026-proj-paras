package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductVariation {

    @NotNull(message = "id must not be null")
     Long id;

    @Min(value = 1, message = "Quantity must be greater than zero")
     Integer quantity;
    @Min(value = 1, message = "price must be greater than zero")
     Long price;
     Map<String,String> metadata;
     MultipartFile primaryImage;
     List<MultipartFile> secondaryImages;
     Boolean isActive;
}
