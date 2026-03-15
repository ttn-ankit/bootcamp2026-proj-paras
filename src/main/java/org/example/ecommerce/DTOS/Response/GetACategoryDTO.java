package org.example.ecommerce.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetACategoryDTO {
    private Long id;
    private String name;
    private GetACategoryDTO parent;
    private List<GetACategoryDTO> child =new ArrayList<>();

    private Map<String,String> metadata;
}
