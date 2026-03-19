package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryMetadataFieldValueBySellerDTO {
    private String categoryName;
    private String fieldName;
    private String fieldValues;
    private List<GetACategoryDTO> parent;
}
