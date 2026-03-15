package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddMetaDataFieldValueDto;
import org.example.ecommerce.DTOS.Request.CategoryMetadataFieldDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.GetCategoryMetadataFieldValueBySellerDTO;
import org.example.ecommerce.Service.MetadataService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/metadata")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Validated
public class MetadataFieldController {

    MetadataService metadataService;
    MessageSource messageSource;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/metadata-field")
    public BasicResponse addMetadataFieldName(@Valid @RequestBody CategoryMetadataFieldDto dto,
                                              @RequestHeader(name = "Accept-Language",required = false) Locale locale){

        Long id = metadataService.addFieldName(dto.getName(),locale);
        Object[] ob = new Object[]{dto.getName(),id};
        return new BasicResponse(messageSource.getMessage("message.new.metadata.field.added",ob,locale),true);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/metadata-fields")
    public List<CategoryMetadataFieldDto> getAllMetadataFields(
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "offSet",defaultValue = "0") Integer offSet,
            @RequestParam(value = "sort",defaultValue = "id") String sort,
            @RequestParam(value = "order",defaultValue = "asc") String order,
            @RequestParam(value = "query", required = false) String query,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale
    ){

        List<CategoryMetadataFieldDto> fields = metadataService.getAllMetadataFields(pageSize,offSet,sort,order,query);

        return fields;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/metadata-field-value")
    public BasicResponse addMetadataFieldValues(@Valid @RequestBody AddMetaDataFieldValueDto metadataFieldValueDTO,
                                           @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        metadataService.addMetadataCategoryFieldValues(metadataFieldValueDTO,locale);

        String response = messageSource.getMessage("message.metadata.fieldadded",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/metadata-field-value")
    public BasicResponse updateMetadataFieldValues(@Valid @RequestBody AddMetaDataFieldValueDto metadataFieldValueDTO,
                                              @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        metadataService.updateMetadataValues(metadataFieldValueDTO,locale);
        String response = messageSource.getMessage("message.metadata.fieldvalueadded",null,locale);
        return new BasicResponse(response,true);

    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get-categories-seller")
    public List<GetCategoryMetadataFieldValueBySellerDTO> getMetadataValues(
            @RequestHeader(value = "Accept-Language",required = false) Locale locale
    ){

        return  metadataService.GetCategoryAndMetadataValue(locale);
    }
}
