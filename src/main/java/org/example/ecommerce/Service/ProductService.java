package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddProductDto;
import org.example.ecommerce.DTOS.Request.AddProductVariationDto;
import org.example.ecommerce.DTOS.Request.UpdateProduct;
import org.example.ecommerce.DTOS.Request.UpdateProductVariation;
import org.example.ecommerce.DTOS.Response.GetProductByAdminDTO;
import org.example.ecommerce.DTOS.Response.ProductVariationDTO;
import org.example.ecommerce.DTOS.Response.ProductVariationDetail;
import org.example.ecommerce.DTOS.Response.ViewProductByCustomerDTO;
import org.example.ecommerce.Emails.EmailService;
import org.example.ecommerce.Entity.*;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.CategoryRepository;
import org.example.ecommerce.Repository.ProductRepository;
import org.example.ecommerce.Repository.ProductVariationRepository;
import org.example.ecommerce.Repository.SellerRepository;
import org.example.ecommerce.Security.JWTService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class ProductService {

     ProductRepository productRepository;
     CategoryRepository categoryRepository;
     SellerRepository sellerRepository;
     ProductVariationRepository productVariationRepository;
     EmailService productEmail;
     JWTService jwtService;

    public void addProduct(AddProductDto productDTO , String token) {

        if(categoryRepository.existsByParentCategoryId(productDTO.getCategoryId())){
            throw new APIException("Parent category can not have products in product table", HttpStatus.BAD_REQUEST);
        }
        Category category = categoryRepository.findById(productDTO.getCategoryId()).get();

        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        Product product = productRepository.findByBrandAndCategoryAndSeller(productDTO.getBrand(),category,seller);

        if (product!=null && product.getName().equals(productDTO.getName())){
            throw new APIException("product with the same name exist for the same seller same category and same brand",HttpStatus.BAD_REQUEST);
        }

        Product product1 = new Product();
        product1.setBrand(productDTO.getBrand());
        product1.setName(productDTO.getName());
        product1.setDescription(productDTO.getDescription());
        product1.setIsActive(false);
        product1.setIsCancellable(Optional.ofNullable(productDTO.getIsCancellable()).orElse(false));
        product1.setIsDeleted(false);
        product1.setIsRefundable(Optional.ofNullable(productDTO.getIsRefundable()).orElse(false));
        product1.setCategory(category);
        product1.setSeller(seller);
        productEmail.sendEmail("product name"+product1.getName()+"\nadded by "+seller.getFirstName(),email,"product Added Successfully");
        productRepository.save(product1);

    }

    public void addProductVariation(String token, AddProductVariationDto addProductVariation) {
        Product product = productRepository.findById(addProductVariation.getProductId()).orElseThrow(
                ()-> new APIException("product with id "+addProductVariation.getProductId()+" is not found",HttpStatus.BAD_REQUEST)
        );

        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        if(!seller.getId().equals(product.getSeller().getId())){
            throw new APIException("you can not add product here" ,HttpStatus.BAD_REQUEST );
        }
        if(!product.getIsActive() || product.getIsDeleted()){
            throw new APIException("your product either inactive or deleted",HttpStatus.BAD_REQUEST);
        }

        validateProductVariationMetadata(product,addProductVariation.getMetadata());

        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setPrice(addProductVariation.getPrice());
        variation.setQuantityAvailable(addProductVariation.getQuantity());
        String primaryImageName = product.getId().toString()+"_"+System.currentTimeMillis();
        variation.setPrimaryImageName(primaryImageName);
        variation.setMetadata(addProductVariation.getMetadata());
        GetAndSaveImage.uploadProductImages("/images/product/primary", addProductVariation.getPrimaryImage(),primaryImageName);
        for(MultipartFile image : addProductVariation.getSecondaryImages()){
            GetAndSaveImage.uploadProductImages("/images/product/secondary",image,primaryImageName+"_"+System.currentTimeMillis());
        }
        productVariationRepository.save(variation);


    }


    public Map<String, List<String>> getAllowedMetadataFields(Category category) {
        Map<String, List<String>> allowedMetadata = new HashMap<>();

        for (CategoryMetadataFieldValue cmdfv : category.getMetadataFieldValues()) {
            String fieldName = cmdfv.getMetadataField().getName();
            List<String> values = Arrays.stream(cmdfv.getValue().toLowerCase().split(","))
                    .map(String::trim)
                    .toList();
            allowedMetadata.put(fieldName, values);
        }

        return allowedMetadata;
    }

    public void validateProductVariationMetadata(Product product, Map<String, String> newMetadata) {

        Map<String, List<String>> allowedMetadata = getAllowedMetadataFields(product.getCategory());

        if (newMetadata == null || newMetadata.isEmpty()) {
            throw new APIException("**",HttpStatus.BAD_REQUEST);
        }

        for (Map.Entry<String, String> entry : newMetadata.entrySet()) {
            String field = entry.getKey().toLowerCase();
            String value = entry.getValue().toLowerCase();

            if (!allowedMetadata.containsKey(field)) {
                throw new APIException("Field '" + field + "' is not allowed for this category.",HttpStatus.BAD_REQUEST);
            }

            if (!allowedMetadata.get(field).contains(value)) {
                throw new APIException("Value '" + value + "' is not valid for field '" + field + "'",HttpStatus.BAD_REQUEST);
            }
        }

        List<ProductVariation> existingVariations = product.getVariations();

        if (!existingVariations.isEmpty()) {
            Set<String> newKeys = newMetadata.keySet();
            Set<String> referenceKeys = existingVariations.get(0).getMetadata().keySet();

            if (!newKeys.equals(referenceKeys)) {
                throw new APIException("All variations must have the same metadata structure. Required keys: " + referenceKeys,HttpStatus.BAD_REQUEST);
            }
        }
    }


    public AddProductDto getASelectedProduct(String token, Long id) {

        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);
        Product product = productRepository.findById(id).orElseThrow(
                ()-> new APIException("product with id "+id+" is not found",HttpStatus.BAD_REQUEST)
        );
        if(!seller.getId().equals(product.getSeller().getId()) || product.getIsDeleted()){
            throw new APIException("You can not get the product details",HttpStatus.BAD_REQUEST);
        }

        AddProductDto productDTO = new AddProductDto();
        productDTO.setId(product.getId());
        productDTO.setBrand(product.getBrand());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setIsRefundable(product.getIsRefundable());
        productDTO.setIsCancellable(product.getIsCancellable());
        productDTO.setCategoryId(product.getCategory().getId());


        return productDTO;
    }

    public ProductVariationDTO getASelectedProductVariation(String token, Long id) {

        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        ProductVariation variation = productVariationRepository.findById(id).orElseThrow(
                ()-> new APIException("product variation with id "+ id + " is not found",HttpStatus.BAD_REQUEST)
        );

        Product product = variation.getProduct();
        if(product.getIsDeleted()){
            throw new APIException("product deleted",HttpStatus.BAD_REQUEST);
        }
        if(!product.getSeller().getId().equals(seller.getId())){
            throw new APIException("you can not get product variation details",HttpStatus.BAD_REQUEST);
        }
        ProductVariationDTO productVariationDTO = new ProductVariationDTO();
        productVariationDTO.setId(variation.getId());
        productVariationDTO.setQuantity(variation.getQuantityAvailable());
        productVariationDTO.setMetadata(variation.getMetadata());
        productVariationDTO.setPrice(variation.getPrice());
        AddProductDto productDTO = new AddProductDto();
        productDTO.setId(product.getId());
        productDTO.setBrand(product.getBrand());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setIsRefundable(product.getIsRefundable());
        productDTO.setIsCancellable(product.getIsCancellable());
        productDTO.setCategoryId(product.getCategory().getId());
        productVariationDTO.setProduct(productDTO);
        productVariationDTO.setPrimaryImage(GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/",variation.getPrimaryImageName()));
        List<String> secondaryImage = GetAndSaveImage.getAllSecondaryImages("/images/product/secondary/",variation.getPrimaryImageName());
        productVariationDTO.setSecondaryImages(secondaryImage);


        return productVariationDTO;
    }


    public List<AddProductDto> getAllProductsOfSeller(String token, Integer pageSize, Integer offSet, String sort, String order, String query) {
        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        Pageable pageable = PageRequest.of(offSet,pageSize, Sort.by(Sort.Direction.fromString(order),sort));

        DynamicSpecification<Product> dynamicSpecification = new DynamicSpecification<>();
        Specification<Product> spec = dynamicSpecification.build(query);

        Specification<Product> sellerSpec = (root, query1, cb) ->
                cb.equal(root.get("seller").get("id"), seller.getId());

        Specification<Product> finalSpec = (spec != null) ? spec.and(sellerSpec) : sellerSpec;

        Page<Product> productsPage = productRepository.findAll(finalSpec, pageable);

        List<Product> products = productsPage.getContent();


        List<AddProductDto> productDTOList = products.stream()
                .map(
                        product -> {
                            AddProductDto dto = new AddProductDto();
                            dto.setId(product.getId());
                            dto.setCategoryId(product.getCategory().getId());
                            dto.setName(product.getName());
                            dto.setBrand(product.getBrand());
                            dto.setDescription(product.getDescription());
                            dto.setIsRefundable(product.getIsRefundable());
                            dto.setIsCancellable(product.getIsCancellable());

                            return dto;
                        }
                )
                .collect(Collectors.toUnmodifiableList());



        return productDTOList;
    }

    public List<ProductVariationDTO> getAllProductVariations(String token, Integer pageSize, Integer offSet, String sort, String order, String query, Long id) {
        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        Product product = productRepository.findById(id).orElseThrow(
                ()-> new APIException("product with id "+id+" is not found",HttpStatus.BAD_REQUEST)
        );

        if(!product.getSeller().getId().equals(seller.getId())){
            throw new APIException("You are not the creator seller of the product",HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(offSet,pageSize,Sort.by(Sort.Direction.fromString(order),sort));


        DynamicSpecification<ProductVariation> dynamicSpecification = new DynamicSpecification<>();
        Specification<ProductVariation> spec = dynamicSpecification.build(query);

        Specification<ProductVariation> sellerSpec = (root, query1, cb) ->
                cb.equal(root.get("product").get("id"), product.getId());

        Specification<ProductVariation> finalSpec = (spec != null) ? spec.and(sellerSpec) : sellerSpec;

        Page<ProductVariation> productVariationPage = productVariationRepository.findAll(finalSpec, pageable);

        List<ProductVariation> variations = productVariationPage.getContent();


        List<ProductVariationDTO> productVariationDTOS = variations.stream()
                .map( variation->{
                            ProductVariationDTO dto = new ProductVariationDTO();
                            dto.setId(variation.getId());
                            dto.setQuantity(variation.getQuantityAvailable());
                            dto.setMetadata(variation.getMetadata());
                            dto.setPrice(variation.getPrice());
                            AddProductDto productDTO = new AddProductDto();
                            productDTO.setBrand(product.getBrand());
                            productDTO.setId(product.getId());
                            productDTO.setName(product.getName());
                            productDTO.setDescription(product.getDescription());
                            productDTO.setIsRefundable(product.getIsRefundable());
                            productDTO.setIsCancellable(product.getIsCancellable());
                            productDTO.setCategoryId(product.getCategory().getId());
                            dto.setProduct(productDTO);
                            dto.setPrimaryImage(GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/",variation.getPrimaryImageName()));
                            List<String> secondaryImage = GetAndSaveImage.getAllSecondaryImages("/images/product/secondary/",variation.getPrimaryImageName());
                            dto.setSecondaryImages(secondaryImage);
                            return dto;
                        }

                )
                .collect(Collectors.toUnmodifiableList());



        return productVariationDTOS;
    }

    public void deleteTheProduct(String token, Long id) {
        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        Product product = productRepository.findById(id).orElseThrow(
                ()-> new APIException("product with id "+id+" is not found",HttpStatus.BAD_REQUEST)
        );

        if(!product.getSeller().getId().equals(seller.getId())){
            throw new APIException("You are not the creator seller of the product",HttpStatus.BAD_REQUEST);
        }

        productRepository.delete(product);
    }


    public GetProductByAdminDTO getProductByAdmin(Long id) {
        GetProductByAdminDTO responseDTO = new GetProductByAdminDTO();

        Product product = productRepository.findProductById(id);
        if (product==null || !product.getIsActive()){
            throw new APIException("product with id "+id+" is not found",HttpStatus.BAD_REQUEST);
        }

        responseDTO.setId(product.getId());
        responseDTO.setBrand(product.getBrand());
        responseDTO.setDescription(product.getDescription());
        responseDTO.setName(product.getName());
        responseDTO.setIsRefundable(product.getIsRefundable());
        responseDTO.setIsCancellable(product.getIsCancellable());
        responseDTO.setCategoryId(product.getCategory().getId());
        responseDTO.setCategoryName(product.getCategory().getName());
        Map<String,String> primaryImage = new HashMap<>();
        List<ProductVariation> productVariation = productVariationRepository.findAllByProduct(product);
        for(ProductVariation variation : productVariation){
            primaryImage.put(variation.getId().toString(),GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/",variation.getPrimaryImageName()));
        }
        responseDTO.setPrimaryImage(primaryImage);


        return responseDTO;
    }

    public List<GetProductByAdminDTO> selectAllProducts() {

        List<Product> products = productRepository.findAll();
        if(products.size()<1){
            throw new APIException("no product found",HttpStatus.BAD_REQUEST);
        }

        List<GetProductByAdminDTO> response = products.stream()
                .filter(product -> product.getIsActive())
                .map(
                        product -> {
                            GetProductByAdminDTO responseDTO = new GetProductByAdminDTO();
                            responseDTO.setId(product.getId());
                            responseDTO.setBrand(product.getBrand());
                            responseDTO.setDescription(product.getDescription());
                            responseDTO.setName(product.getName());
                            responseDTO.setIsRefundable(product.getIsRefundable());
                            responseDTO.setIsCancellable(product.getIsCancellable());
                            responseDTO.setCategoryId(product.getCategory().getId());
                            responseDTO.setCategoryName(product.getCategory().getName());
                            Map<String,String> primaryImage = new HashMap<>();
                            List<ProductVariation> productVariation = productVariationRepository.findAllByProduct(product);
                            for(ProductVariation variation : productVariation){
                                primaryImage.put(variation.getId().toString(),GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/",variation.getPrimaryImageName()));
                            }
                            responseDTO.setPrimaryImage(primaryImage);


                            return responseDTO;
                        }
                )
                .collect(Collectors.toUnmodifiableList());

        return response;
    }

    public void activateTheProduct(Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                ()-> new APIException("Product with id "+ id + " is not found",HttpStatus.BAD_REQUEST));


        if(product.getIsActive()){
            throw new APIException("product already active",HttpStatus.BAD_REQUEST);
        }

        String email = product.getSeller().getEmail();
        productEmail.sendEmail("Product Activated",email, "Product successfully activated");
        product.setIsActive(true);
        productRepository.save(product);


    }

    public void deActivateTheProduct(Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                ()-> new APIException("Product with id "+ id + " is not found",HttpStatus.BAD_REQUEST)
        );


        if(!product.getIsActive()){
            throw new APIException("product not activated already",HttpStatus.BAD_REQUEST);
        }

        String email = product.getSeller().getEmail();
        productEmail.sendEmail("Product Deactivated",email,"Product");
        product.setIsActive(false);
        productRepository.save(product);


    }

    public ViewProductByCustomerDTO getProductByCustomer(Long id) {

        Product product = productRepository.findById(id).orElseThrow(
                ()-> new APIException("Product with id "+id+" is not found",HttpStatus.BAD_REQUEST)
        );

        if(!product.getIsActive()){
            throw new APIException("Product with id "+id+" is not yet active",HttpStatus.BAD_REQUEST);

        }

        List<ProductVariation> productVariation = productVariationRepository.findAllByProduct(product);

        if(productVariation.isEmpty()){
            throw new APIException("No variations found for this product",HttpStatus.BAD_REQUEST);
        }

        ViewProductByCustomerDTO viewProduct = new ViewProductByCustomerDTO();
        viewProduct.setId(product.getId());
        viewProduct.setDescription(product.getDescription());
        viewProduct.setCategoryName(product.getCategory().getName());
        viewProduct.setIsRefundable(product.getIsRefundable());
        viewProduct.setIsCancellable(product.getIsCancellable());
        viewProduct.setBrand(product.getBrand());
        viewProduct.setCategoryId(product.getCategory().getId());
        viewProduct.setName(product.getName());

        List<ProductVariationDetail> productVariationDetailsList = productVariation.stream()
                .map(
                        variation->{
                            ProductVariationDetail detail = new ProductVariationDetail();
                            detail.setMetadata(variation.getMetadata());
                            detail.setPrimaryImage(GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/",variation.getPrimaryImageName()));
                            detail.setQuantity(variation.getQuantityAvailable());
                            detail.setPrice(variation.getPrice());
                            return detail;
                        }
                )
                .collect(Collectors.toUnmodifiableList());

        viewProduct.setProductVariations(productVariationDetailsList);
        return viewProduct;
    }

    public List<ViewProductByCustomerDTO> getAllProductsByCustomer(Long id, Integer pageSize, Integer offSet, String sort, String order, String query) {
        List<Long> categoryIds = findAllLeafCategoryIds(id);

        DynamicSpecification<Product> dynamicSpecification = new DynamicSpecification<>();
        Specification<Product> spec = dynamicSpecification.build(query);

        Specification<Product> categoryFilter = (root, query1, cb) -> root.get("category").get("id").in(categoryIds);
        Specification<Product> isActiveFilter = (root, query1, cb) -> cb.isTrue(root.get("isActive"));
        Specification<Product> finalSpec = Specification.where(categoryFilter).and(isActiveFilter);

        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        Pageable pageable = PageRequest.of(offSet, pageSize, Sort.by(Sort.Direction.fromString(order), sort));
        Page<Product> pagedProducts = productRepository.findAll(finalSpec, pageable);

        return pagedProducts.getContent().stream()
                .filter(product -> !productVariationRepository.findAllByProduct(product).isEmpty())
                .map(product -> {
                    ViewProductByCustomerDTO dto = new ViewProductByCustomerDTO();
                    dto.setId(product.getId());
                    dto.setDescription(product.getDescription());
                    dto.setCategoryName(product.getCategory().getName());
                    dto.setIsRefundable(product.getIsRefundable());
                    dto.setIsCancellable(product.getIsCancellable());
                    dto.setBrand(product.getBrand());
                    dto.setCategoryId(product.getCategory().getId());
                    dto.setName(product.getName());

                    List<ProductVariation> variations = productVariationRepository.findAllByProduct(product);
                    List<ProductVariationDetail> variationDetails = variations.stream()
                            .map(variation -> {
                                ProductVariationDetail detail = new ProductVariationDetail();
                                detail.setMetadata(variation.getMetadata());
                                detail.setPrimaryImage(GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/", variation.getPrimaryImageName()));
                                detail.setQuantity(variation.getQuantityAvailable());
                                detail.setPrice(variation.getPrice());
                                return detail;
                            })
                            .collect(Collectors.toUnmodifiableList());

                    dto.setProductVariations(variationDetails);
                    return dto;
                })
                .collect(Collectors.toUnmodifiableList());
    }



    public List<Long> findAllLeafCategoryIds(Long parentId) {
        Category category = categoryRepository.findById(parentId)
                .orElseThrow(() -> new APIException("Category not found",HttpStatus.BAD_REQUEST));

        List<Long> leafIds = new ArrayList<>();
        collectLeafCategoryIds(category, leafIds);
        return leafIds;
    }

    private void collectLeafCategoryIds(Category category, List<Long> leafIds) {
        if (category.getSubCategories() == null || category.getSubCategories().isEmpty()) {
            leafIds.add(category.getId());
        } else {
            for (Category child : category.getSubCategories()) {
                collectLeafCategoryIds(child, leafIds);
            }
        }
    }

    public List<ViewProductByCustomerDTO> getSimilarProductsByCustomer(Long id, Integer pageSize, Integer offSet, String sort, String order, String query) {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new APIException("Product with id " + id + " is not found",HttpStatus.BAD_REQUEST)
        );

        Pageable pageable = PageRequest.of(offSet, pageSize, Sort.by(Sort.Direction.fromString(order), sort));

        DynamicSpecification<Product> dynamicSpecification = new DynamicSpecification<>();
        Specification<Product> querySpec = dynamicSpecification.build(query);

        Specification<Product> staticSpec = (root, q, cb) -> cb.and(
                cb.or(
                        cb.equal(root.get("category"), product.getCategory()),
                        cb.equal(root.get("brand"), product.getBrand())
                ),
                cb.notEqual(root.get("id"), product.getId())
        );

        Specification<Product> finalSpec = (querySpec != null) ? staticSpec.and(querySpec) : staticSpec;

        Page<Product> similarProductsPage = productRepository.findAll(finalSpec, pageable);
        List<Product> similarProducts = similarProductsPage.getContent();

        return similarProducts.stream()
                .map(similarProduct -> {
                    ViewProductByCustomerDTO dto = new ViewProductByCustomerDTO();
                    dto.setId(similarProduct.getId());
                    dto.setName(similarProduct.getName());
                    dto.setBrand(similarProduct.getBrand());
                    dto.setCategoryId(similarProduct.getCategory().getId());
                    dto.setCategoryName(similarProduct.getCategory().getName());
                    dto.setDescription(similarProduct.getDescription());
                    dto.setIsRefundable(similarProduct.getIsRefundable());
                    dto.setIsCancellable(similarProduct.getIsCancellable());

                    List<ProductVariation> variations = productVariationRepository.findAllByProduct(similarProduct);
                    List<ProductVariationDetail> variationDetails = variations.stream()
                            .map(var -> {
                                ProductVariationDetail detail = new ProductVariationDetail();
                                detail.setMetadata(var.getMetadata());
                                detail.setPrimaryImage(GetAndSaveImage.resolveProductPrimaryImage("/images/product/primary/", var.getPrimaryImageName()));
                                detail.setQuantity(var.getQuantityAvailable());
                                detail.setPrice(var.getPrice());
                                return detail;
                            })
                            .collect(Collectors.toUnmodifiableList());

                    dto.setProductVariations(variationDetails);
                    return dto;
                })
                .collect(Collectors.toUnmodifiableList());
    }


    public void updateTheProduct(String token, UpdateProduct updateProduct) {

        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        Product product = productRepository.findById(updateProduct.getId()).orElseThrow(
                ()-> new APIException("product with id "+updateProduct.getId()+" is not found",HttpStatus.BAD_REQUEST)
        );

        if(!product.getSeller().getId().equals(seller.getId())){
            throw new APIException("access denied : you can not update this",HttpStatus.BAD_REQUEST);
        }

        updateProduct.setName(Optional.ofNullable(updateProduct.getName()).orElse(product.getName()));
        updateProduct.setDescription(Optional.ofNullable(updateProduct.getDescription()).orElse(product.getDescription()));
        updateProduct.setIsCancellable(Optional.ofNullable(updateProduct.getIsCancellable()).orElse(product.getIsCancellable()));
        updateProduct.setIsRefundable(Optional.ofNullable(updateProduct.getIsRefundable()).orElse(product.getIsRefundable()));

        Product product1 = productRepository.findByBrandAndCategoryAndSeller(product.getBrand(),product.getCategory(),product.getSeller());

        if (!product.equals(product1) && product1.getName().equals(updateProduct.getName())){
            throw new APIException("product with the same name exist for the same seller same category and same brand",HttpStatus.BAD_REQUEST);
        }

        product.setName(updateProduct.getName());
        product.setDescription(updateProduct.getDescription());
        product.setIsRefundable(updateProduct.getIsRefundable());
        product.setIsCancellable(updateProduct.getIsCancellable());

        productRepository.save(product);


    }

    public void updateProductVariation(String token, UpdateProductVariation updateProductVariation) {

        ProductVariation productVariation = productVariationRepository.findById(updateProductVariation.getId()).orElseThrow(
                ()-> new APIException("product variation with id not found",HttpStatus.BAD_REQUEST)
        );

        String email = jwtService.extractUsername(token);
        Seller seller = sellerRepository.findByEmail(email);

        Product product = Optional.ofNullable(productVariation.getProduct()).orElseThrow(
                ()->new APIException("product of variation might be deleted",HttpStatus.BAD_REQUEST)
        );

        if(!product.getSeller().getId().equals(seller.getId())){
            throw new APIException("Access denied : you can not update the variation",HttpStatus.BAD_REQUEST);
        }

        productVariation.setMetadata(Optional.ofNullable(updateProductVariation.getMetadata()).orElse(productVariation.getMetadata()));

        validateProductVariationMetadata(product,productVariation.getMetadata());

        productVariation.setPrice(Optional.ofNullable(updateProductVariation.getPrice()).orElse(productVariation.getPrice()));
        productVariation.setQuantityAvailable(Optional.ofNullable(updateProductVariation.getQuantity()).orElse(productVariation.getQuantityAvailable()));
        productVariation.setIsActive(Optional.ofNullable(updateProductVariation.getIsActive()).orElse(productVariation.getIsActive()));

        if (updateProductVariation.getPrimaryImage()!=null && !updateProductVariation.getPrimaryImage().isEmpty()){
            String primaryImageName = product.getId().toString()+System.currentTimeMillis();
            productVariation.setPrimaryImageName(primaryImageName);
            GetAndSaveImage.uploadProductImages("/images/product/primary", updateProductVariation.getPrimaryImage(),primaryImageName);
        }

        List<MultipartFile> files = updateProductVariation.getSecondaryImages();
        if (files != null && !files.isEmpty()) {
            String baseName = productVariation.getPrimaryImageName();
            long timestamp = System.currentTimeMillis();

            for (int i = 0; i < files.size(); i++) {
                MultipartFile image = files.get(i);
                if (image != null && !image.isEmpty()) {
                    String filename = baseName + "_" + timestamp + "_" + i;
                    GetAndSaveImage.uploadProductImages("/images/product/secondary", image, filename);
                }
            }
        }


        productVariationRepository.save(productVariation);

    }
}
