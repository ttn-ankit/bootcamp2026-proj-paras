package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariationDetail {
    private Integer quantity;
    private Map<String,String> metadata;
    private Double price;
    private String primaryImage;
}
