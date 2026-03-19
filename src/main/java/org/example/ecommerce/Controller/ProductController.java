package org.example.ecommerce.Controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddProductDto;
import org.example.ecommerce.DTOS.Request.AddProductVariationDto;
import org.example.ecommerce.DTOS.Request.UpdateProduct;
import org.example.ecommerce.DTOS.Request.UpdateProductVariation;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.GetProductByAdminDTO;
import org.example.ecommerce.DTOS.Response.ProductVariationDTO;
import org.example.ecommerce.DTOS.Response.ViewProductByCustomerDTO;
import org.example.ecommerce.Service.ProductService;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/product")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class ProductController {
     ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-products-admin")
    public List<GetProductByAdminDTO> getAllProducts(
            @RequestParam(value = "max", defaultValue = "10") Integer max,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "productId", required = false) Long productId
    ) {
        return productService.selectAllProducts(max, offset, sort, order, sellerId, categoryId, productId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate-product")
    public BasicResponse activateProduct(@RequestParam("id") Long id){

        return productService.activateTheProduct(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate-product")
    public BasicResponse deActivateProduct(@RequestParam("id") Long id){

        return productService.deActivateTheProduct(id);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/view-product-customer")
    public ViewProductByCustomerDTO getProductByCustomer(@RequestParam("id") Long id){
        return productService.getProductByCustomer(id);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/view-all-products-customer")
    public List<ViewProductByCustomerDTO> gelAllProductsByCustomer(
            @RequestParam("id") Long id,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(name = "offSet",defaultValue = "0") Integer offSet,
            @RequestParam(name = "sort",defaultValue = "id") String sort,
            @RequestParam(name = "order",defaultValue = "asc") String order,
            @RequestParam(value = "query", required = false) String query){

        return productService.getAllProductsByCustomer(id,pageSize,offSet,sort,order,query);
    }



    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/view-similar-product")
    public List<ViewProductByCustomerDTO> getSimilarProductsForCustomer(
            @RequestParam("id") Long id,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(name = "offSet",defaultValue = "0") Integer offSet,
            @RequestParam(name = "sort",defaultValue = "id") String sort,
            @RequestParam(name = "order",defaultValue = "asc") String order,
            @RequestParam(value = "query", required = false) String query
    ){

        return productService.getSimilarProductsByCustomer(id,pageSize,offSet,sort,order,query);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/add/product")
    public BasicResponse addNewProduct(@Valid @RequestBody AddProductDto productDTO){
        return productService.addProduct(productDTO);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/add/product-variation",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse addProductVariation(@Valid @ModelAttribute AddProductVariationDto productVariation){

        return productService.addProductVariation(productVariation);
    }
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get/all-products")
    public List<AddProductDto> getAllProductsBYSeller(@RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "offSet",defaultValue = "0") Integer offSet,
                                                      @RequestParam(name = "sort",defaultValue = "id") String sort,
                                                      @RequestParam(name = "order",defaultValue = "asc") String order,
                                                      @RequestParam(name = "query", required = false) String query
    ){
        return productService.getAllProductsOfSeller(pageSize,offSet,sort,order,query);
    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get/product-variations/{id}")
    public List<ProductVariationDTO> getAllProductVariationsOfProductBySeller(@RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                                                              @RequestParam(name = "offSet",defaultValue = "0") Integer offSet,
                                                                              @RequestParam(name = "sort",defaultValue = "id") String sort,
                                                                              @RequestParam(name = "order",defaultValue = "asc") String order,
                                                                              @RequestParam(value = "query",required = false) String query,
                                                                              @PathVariable Long id
    ){
        return productService.getAllProductVariations(pageSize,offSet,sort,order,query,id);
    }


    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/delete/product/{id}")
    public BasicResponse deleteAProduct(@PathVariable Long id){

        return productService.deleteTheProduct(id);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/update-product")
    public BasicResponse updateProduct(@Valid @RequestBody UpdateProduct updateProduct){

        return productService.updateTheProduct(updateProduct);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping(value = "/update-product-variation",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse updateProductVariation(@Valid @ModelAttribute UpdateProductVariation updateProductVariation){
       return productService.updateProductVariation(updateProductVariation);
    }

}
