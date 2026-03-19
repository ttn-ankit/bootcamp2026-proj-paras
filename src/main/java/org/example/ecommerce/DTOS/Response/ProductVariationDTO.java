package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommerce.DTOS.Request.AddProductDto;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariationDTO {
    private Long id;
    private AddProductDto product;
    private String primaryImage;
    private List<String> secondaryImages;
    private Map<String,String > metadata;
    private Integer Quantity;
    private Double price;
}
