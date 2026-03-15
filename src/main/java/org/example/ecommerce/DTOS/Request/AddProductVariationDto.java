package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductVariationDto {

    @NotNull(message = "give a product id")
     Long productId;
    @NotNull(message = "give product quantity")
    @Min(value = 1, message = "quantity must be greater than 0")
     Integer quantity;
    @NotNull(message = "give product metadata")
    @Size(min = 1, message = "Metadata must contain at least one entry")
     Map<String,String> metadata;
    @NotNull(message = "give product price")
    @Min(value = 1, message = "Price must be greater than 0")
     Double price;
    @NotNull(message = "give primary image of the product")
     MultipartFile primaryImage;

    List<MultipartFile> secondaryImages;
}
