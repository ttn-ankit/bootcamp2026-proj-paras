package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.CategoryMetadataFieldDto;
import org.example.ecommerce.Service.MetadataService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Validated
public class MetadataFieldController {

    MetadataService metadataService;

    @PostMapping("/addField")
    public CategoryMetadataFieldDto addMetadataField(@Valid @RequestBody CategoryMetadataFieldDto metadataField){
         return metadataService.addMetadataField(metadataField);
    }

    @GetMapping("/getFields")
    public List<CategoryMetadataFieldDto> getAllMetadataFields(@RequestParam(value = "max",defaultValue = "10") Integer max,
                                                               @RequestParam(value = "Offset",defaultValue = "0") Integer offset,
                                                               @RequestParam(value = "sort",defaultValue = "id") String sort,
                                                               @RequestParam(value = "order",defaultValue = "%") String order,
                                                               @RequestParam(value = "query",defaultValue = "")String query){
        return metadataService.getAllMetadataFields(max, offset, sort, order, query);
    }

}
