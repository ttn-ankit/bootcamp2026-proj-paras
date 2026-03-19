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
public class GetProductByAdminDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isCancellable=false;
    private Boolean isRefundable=false;
    private Boolean isActive=false;
    private Boolean isDeleted=false;
    private String brand;
    private Long categoryId;
    private String categoryName;
    private Map<String,String> primaryImage;
}
