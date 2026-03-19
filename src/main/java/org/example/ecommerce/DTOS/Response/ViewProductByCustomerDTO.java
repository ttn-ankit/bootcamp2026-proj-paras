package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ViewProductByCustomerDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean isCancellable;
    private Boolean isRefundable;
    private String brand;
    private Long categoryId;
    private String categoryName;

    private List<ProductVariationDetail> productVariations;

}
