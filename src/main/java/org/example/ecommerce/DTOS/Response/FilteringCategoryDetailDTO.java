package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilteringCategoryDetailDTO {

    private Map<String,String> metadata;
    private List<String> brands;
    private Long lowestPrice;
    private Long highestPrice;
}
