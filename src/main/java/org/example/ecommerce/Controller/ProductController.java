package org.example.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Locale;

@Validated
@RestController
@RequestMapping("/api/product")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class ProductController {
     ProductService productService;
     MessageSource messageSource;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-product-admin/{id}")
    public GetProductByAdminDTO getAProduct(@PathVariable("id") Long id){

        return productService.getProductByAdmin(id);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-products-admin")
    public List<GetProductByAdminDTO> getAllProducts(){

        return productService.selectAllProducts();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate-product")
    public BasicResponse activateProduct(@RequestParam("id") Long id, @RequestHeader(name = "Accept-Language",required = false) Locale locale){

        productService.activateTheProduct(id);
        String response = messageSource.getMessage("message.product.activated",null,locale);
        return new BasicResponse(response,true);

    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate-product")
    public BasicResponse deActivateProduct(@RequestParam("id") Long id,@RequestHeader(name = "Accept-Language",required = false) Locale locale){

        productService.deActivateTheProduct(id);
        String response = messageSource.getMessage("message.product.deactivated",null,locale);
        return new BasicResponse(response,true);

    }




    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/view-product-customer")
    public ViewProductByCustomerDTO getProductByCustomer(@RequestParam("id") Long id){
        ViewProductByCustomerDTO response = productService.getProductByCustomer(id);
        return response;
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

        List<ViewProductByCustomerDTO> response = productService.getAllProductsByCustomer(id,pageSize,offSet,sort,order,query);

        return response;
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
        List<ViewProductByCustomerDTO> response = productService.getSimilarProductsByCustomer(id,pageSize,offSet,sort,order,query);

        return response;
    }




    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/add/product")
    public BasicResponse addNewProduct(@Valid @RequestBody AddProductDto productDTO, HttpServletRequest request,
                                       @RequestHeader(value = "Accept-Language",required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);
        productService.addProduct(productDTO,token);
        String response = messageSource.getMessage("message.product.new.added",null,locale);

        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/add/product-variation",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse addProductVariation(@Valid @ModelAttribute AddProductVariationDto productVariation, HttpServletRequest request,
                                             @RequestHeader(value = "Accept-Language",required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);

        productService.addProductVariation(token,productVariation);
        String response = messageSource.getMessage("message.product.variation.added",null,locale);
        return new BasicResponse(response,true);


    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get/product/{id}")
    public AddProductDto getAProduct(@PathVariable("id") Long id, HttpServletRequest request){

        String token = request.getHeader("Authorization").substring(7);
        AddProductDto product = productService.getASelectedProduct(token , id);
        return product;
    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get/product-variation/{id}")
    public ProductVariationDTO getAProductVariation(@PathVariable("id") Long id, HttpServletRequest request){

        String token = request.getHeader("Authorization").substring(7);
        ProductVariationDTO variationDTO = productService.getASelectedProductVariation(token,id);

        return variationDTO;

    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get/all-products")
    public List<AddProductDto> getAllProductsBYSeller(HttpServletRequest request,
                                                      @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "offSet",defaultValue = "0") Integer offSet,
                                                      @RequestParam(name = "sort",defaultValue = "id") String sort,
                                                      @RequestParam(name = "order",defaultValue = "asc") String order,
                                                      @RequestParam(name = "query", required = false) String query
    ){

        String token = request.getHeader("Authorization").substring(7);
        List<AddProductDto> products = productService.getAllProductsOfSeller(token,pageSize,offSet,sort,order,query);
        return products;

    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/get/product-variations/{id}")
    public List<ProductVariationDTO> getAllProductVariationsOfProductBySeller(HttpServletRequest request,
                                                                              @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                                                              @RequestParam(name = "offSet",defaultValue = "0") Integer offSet,
                                                                              @RequestParam(name = "sort",defaultValue = "id") String sort,
                                                                              @RequestParam(name = "order",defaultValue = "asc") String order,
                                                                              @RequestParam(value = "query",required = false) String query,
                                                                              @PathVariable("id") Long id
    ){

        String token = request.getHeader("Authorization").substring(7);
        List<ProductVariationDTO> products = productService.getAllProductVariations(token,pageSize,offSet,sort,order,query,id);
        return products;

    }


    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/delete/product/{id}")
    public BasicResponse deleteAProduct(@PathVariable("id") Long id,HttpServletRequest request,
                                   @RequestHeader(value = "Accept-Language",required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);
        productService.deleteTheProduct(token,id);
        String response = messageSource.getMessage("message.product.deleted",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/update-product")
    public BasicResponse updateProduct(HttpServletRequest request, @Valid @RequestBody UpdateProduct updateProduct,
                                  @RequestHeader(value = "Accept-Language",required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);
        productService.updateTheProduct(token,updateProduct);
        String response = messageSource.getMessage("message.product.updated",null,locale);
        return new BasicResponse(response,true);

    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping(value = "/update-product-variation",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BasicResponse updateProductVariation(HttpServletRequest request, @Valid @ModelAttribute UpdateProductVariation updateProductVariation,
                                           @RequestHeader(value = "Accept-Language",required = false) Locale locale){

        String token = request.getHeader("Authorization").substring(7);

        productService.updateProductVariation(token,updateProductVariation);
        String response = messageSource.getMessage("message.product.variation.updated",null,locale);
        return new BasicResponse(response,true);

    }

}
