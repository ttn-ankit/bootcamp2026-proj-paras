package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddCategoryDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.FilteringCategoryDetailDTO;
import org.example.ecommerce.DTOS.Response.GetACategoryDTO;
import org.example.ecommerce.Service.CategoryService;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/category")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryController {


    CategoryService categoryService;
     MessageSource messageSource;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/new-category")
    public BasicResponse addNewCategory(@Valid @RequestBody AddCategoryDto categoryDTO, @RequestHeader(name = "Accept-Language", required = false) Locale locale){

        categoryService.addANewParentCategory(categoryDTO,locale);
        String response = messageSource.getMessage("message.categorycreated",null,locale);
        return new BasicResponse(response,true);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-category-admin/{id}")
    public GetACategoryDTO getCategoryUsingId(@PathVariable("id") Long id, @RequestHeader("Accept-Language") Locale locale){

        GetACategoryDTO categoryDTO = categoryService.getCategory(id,locale);
        return categoryDTO;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/all-categories-admin")
    public List<GetACategoryDTO> getAllCategories(
            @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(value = "offSet",defaultValue = "0") Integer offSet,
            @RequestParam(value = "sort",defaultValue = "id") String sort,
            @RequestParam(value = "order",defaultValue = "asc") String order,
            @RequestParam(value = "query",defaultValue = "") String query
    ){

        List<GetACategoryDTO> categoryDTOS = categoryService.getAllCategory(pageSize,offSet,sort,order,query);

        return categoryDTOS;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-category")
    public BasicResponse updateTheCategory(@RequestParam(value = "id",required = true) Long id,
                                      @Pattern(regexp = "^[a-zA-Z'&.,-]+(?: [a-zA-Z'&.,-]+)*$" , message = "field name must be valid and at least of 3 size") @RequestParam("name") String name,
                                      @RequestHeader(name = "Accept-Language", required = false) Locale locale){


        categoryService.updateExistingCategoryName(id,name,locale);
        String response = messageSource.getMessage("message.categoryUpdated",null,locale);
        return new BasicResponse(response,true);

    }



    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-category-customer")
    public List<AddCategoryDto> getCategories(@RequestParam(required = false,name = "id") Long id){


        List<AddCategoryDto> categoryDTOS = categoryService.getCategoryByCustomer(id);


        return categoryDTOS;
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/filtering-data")
    public FilteringCategoryDetailDTO filteringCategoryDetails(@RequestParam("id") Long id){
        FilteringCategoryDetailDTO filteringCategoryDetailDTO = categoryService.getCategoryRelatedFilteringData(id);
        return filteringCategoryDetailDTO;
    }


}
