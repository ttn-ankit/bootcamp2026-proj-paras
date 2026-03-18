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
        return categoryService.addANewParentCategory(categoryDTO,locale);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-category-admin/{id}")
    public GetACategoryDTO getCategoryUsingId(@PathVariable Long id, @RequestHeader("Accept-Language") Locale locale){
        return categoryService.getCategory(id,locale);
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

        return categoryService.getAllCategory(pageSize,offSet,sort,order,query);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-category")
    public BasicResponse updateTheCategory(@RequestParam(value = "id") Long id,
                                      @Pattern(regexp = "^[a-zA-Z'&.,-]+(?: [a-zA-Z'&.,-]+)*$" , message = "field name must be valid and at least of 3 size") @RequestParam("name") String name,
                                      @RequestHeader(name = "Accept-Language", required = false) Locale locale){
        return categoryService.updateExistingCategoryName(id,name,locale);
    }



    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get-category-customer")
    public List<AddCategoryDto> getCategories(@RequestParam(required = false,name = "id") Long id){
        return categoryService.getCategoryByCustomer(id);
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/filtering-data")
    public FilteringCategoryDetailDTO filteringCategoryDetails(@RequestParam("id") Long id){
        return categoryService.getCategoryRelatedFilteringData(id);
    }


}
