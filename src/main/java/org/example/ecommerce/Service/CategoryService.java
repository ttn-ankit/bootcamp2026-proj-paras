package org.example.ecommerce.Service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddCategoryDto;
import org.example.ecommerce.DTOS.Response.FilteringCategoryDetailDTO;
import org.example.ecommerce.DTOS.Response.GetACategoryDTO;
import org.example.ecommerce.Entity.Category;
import org.example.ecommerce.Entity.CategoryMetadataFieldValue;
import org.example.ecommerce.Entity.Product;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.*;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryService {

     CategoryRepository categoryRepository;
     CategoryMetadataFieldValueRepository categoryMetadataFieldValueRepository;
     ProductRepository productRepository;
     ProductVariationRepository productVariationRepository;
     MessageSource messageSource;

    public void addANewParentCategory(@Valid AddCategoryDto categoryDTO, Locale locale) {

        if(categoryDTO.getId()==null){

            if(categoryRepository.existsByNameIgnoreCaseAndParentCategoryIsNull(categoryDTO.getName())){
                String localizedMessage = messageSource.getMessage("category.name.already.exists", null, locale);
                throw new APIException(localizedMessage, HttpStatus.BAD_REQUEST);

            }

            Category category = new Category();
            category.setName(categoryDTO.getName());
            categoryRepository.save(category);
            return;
        }

        Long parentId = categoryDTO.getId();

        if(productRepository.existsByCategoryId(parentId)){
            String localizedMessage = messageSource.getMessage("category.assigned.cannot.become.parent", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);
        }
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> {String localizedMessage = messageSource.getMessage("invalid.parent.category.id", null, locale);
                    throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);
                });


        boolean nameExistsInSiblings = categoryRepository.existsByNameIgnoreCaseAndParentCategory(categoryDTO.getName(), parent);

        if (nameExistsInSiblings) {
            String localizedMessage = messageSource.getMessage("category.name.same.parent.exists", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        Set<String> ancestorNames = new HashSet<>();
        Category temp = parent;
        while (temp != null) {
            ancestorNames.add(temp.getName().toLowerCase());
            temp = temp.getParentCategory();
        }

        if (ancestorNames.contains(categoryDTO.getName())) {
            String localizedMessage = messageSource.getMessage("category.name.already.in.parent", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        Set<String> descendantNames = new HashSet<>();
        collectChildNamesRecursively(parent, descendantNames);

        if (descendantNames.contains(categoryDTO.getName())) {
            String localizedMessage = messageSource.getMessage("category.name.conflict.with.child", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        Category child = new Category();
        child.setName(categoryDTO.getName());
        child.setParentCategory(parent);
        categoryRepository.save(child);

    }

    private void collectChildNamesRecursively(Category category, Set<String> names) {
        if (category.getSubCategories() != null) {
            for (Category child : category.getSubCategories()) {
                names.add(child.getName().toLowerCase());
                collectChildNamesRecursively(child, names);
            }
        }
    }

    public GetACategoryDTO getCategory(Long id, Locale locale) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {String localizedMessage = messageSource.getMessage("invalid.category.id", null, locale);
                    throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);
                });

        GetACategoryDTO categoryDTO = new GetACategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());

        // getting all parents upto root
        GetACategoryDTO temp = categoryDTO;
        Category parent = category.getParentCategory();
        while(parent!=null){
            GetACategoryDTO getACategoryDTO1  = new GetACategoryDTO();
            getACategoryDTO1.setId(parent.getId());
            getACategoryDTO1.setName(parent.getName());
            parent = parent.getParentCategory();
            temp.setParent(getACategoryDTO1);
            temp = getACategoryDTO1;
        }

        Map<String,String> metadata = new HashMap<>();

        List<CategoryMetadataFieldValue> fieldValues = categoryMetadataFieldValueRepository.findByCategory(category);

        for(CategoryMetadataFieldValue fieldValues1 : fieldValues){
            metadata.put(fieldValues1.getMetadataField().getName(),fieldValues1.getValue());
        }


        categoryDTO.setMetadata(metadata);
        populateChildren(category,categoryDTO);


        return categoryDTO;
    }
    private void populateChildren(Category category, GetACategoryDTO dto) {
        List<Category> children = category.getSubCategories();
        if (children != null && !children.isEmpty()) {
            for (Category child : children) {
                GetACategoryDTO childDTO = new GetACategoryDTO();
                childDTO.setId(child.getId());
                childDTO.setName(child.getName());
                dto.getChild().add(childDTO);
            }
        }
    }


    public List<GetACategoryDTO> getAllCategory(Integer pageSize, Integer offSet, String sort, String order, String query) {

        Pageable pageable = PageRequest.of(offSet,pageSize, Sort.by(Sort.Direction.fromString(order),sort));

        DynamicSpecification<Category> dynamicSpecification = new DynamicSpecification<>();
        Specification<Category> spec = dynamicSpecification.build(query);

        Page<Category> categories;
        if (spec != null) {
            categories = categoryRepository.findAll(spec, pageable);
        } else {
            categories = categoryRepository.findAll(pageable);
        }


        List<GetACategoryDTO> categoryDTOS = categories.getContent().stream()
                .map( category -> {
                            GetACategoryDTO categoryDTO = new GetACategoryDTO();
                            categoryDTO.setId(category.getId());
                            categoryDTO.setName(category.getName());
                            GetACategoryDTO temp = categoryDTO;
                            Category parent = category.getParentCategory();
                            while(parent!=null){
                                GetACategoryDTO getACategoryDTO1  = new GetACategoryDTO();
                                getACategoryDTO1.setId(parent.getId());
                                getACategoryDTO1.setName(parent.getName());
                                parent = parent.getParentCategory();
                                temp.setParent(getACategoryDTO1);
                                temp = getACategoryDTO1;
                            }
                            populateChildren(category,categoryDTO);
                            Map<String,String> metadata = new HashMap<>();

                            List<CategoryMetadataFieldValue> fieldValues = categoryMetadataFieldValueRepository.findByCategory(category);

                            for(CategoryMetadataFieldValue fieldValues1 : fieldValues){
                                metadata.put(fieldValues1.getMetadataField().getName(),fieldValues1.getValue());
                            }


                            categoryDTO.setMetadata(metadata);

                            return categoryDTO;
                        }

                )
                .toList();



        return categoryDTOS;

    }

    public void updateExistingCategoryName(Long id, @Pattern(regexp = "^[a-zA-Z'&., -]{3,}$" , message = "field name must be valid and at least of 3 size") String name, Locale locale) {


        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> {
                    String localizedMessage = messageSource.getMessage("category.id.not.found", new Object[]{id}, locale);
                    throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);
                }

        );

        Category parent = category.getParentCategory();
        if(parent==null){
            /*
            1. check all at root level with same name
            2 check name of all children
             */
            // 1
            if(categoryRepository.existsByNameIgnoreCaseAndParentCategoryIsNull(name)){
                String localizedMessage = messageSource.getMessage("category.name.already.exists", new Object[]{name}, locale);
                throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

            }
            //2
            Set<String> descendantNames = new HashSet<>();
            collectChildNamesRecursively(category, descendantNames);
            if (descendantNames.contains(name)) {
                throw new APIException("Category name conflicts with one of the child categories.",HttpStatus.BAD_REQUEST);
            }

        }
        else{
            /*
            1 check all parents name
            2 check child name
             */
            // 1
            Category temp = parent;
            while(temp!=null){
                if (temp.getName().equals(name)){
                    throw new APIException("category with same name "+name+" exists in tree",HttpStatus.BAD_REQUEST);
                }
                temp = temp.getParentCategory();
            }
            //2
            Set<String> descendantNames = new HashSet<>();
            collectChildNamesRecursively(parent, descendantNames);
            if (descendantNames.contains(name)) {
                throw new APIException("Category name conflicts with one of the child categories.",HttpStatus.BAD_REQUEST);
            }

        }

        category.setName(name);
        categoryRepository.save(category);

    }

    public List<AddCategoryDto> getCategoryByCustomer(Long id) {

        List<AddCategoryDto> categoryDTOS = new ArrayList<>();
        if(id!=null){

            Category category = categoryRepository.findById(id).orElseThrow(
                    ()-> new APIException("category with id is not found",HttpStatus.BAD_REQUEST)
            );

            List<Category> categories = categoryRepository.findAllByParentCategoryId(id);
            if(categories==null){
                AddCategoryDto addCategoryDTO = new AddCategoryDto();
                addCategoryDTO.setId(category.getId());
                addCategoryDTO.setName(category.getName());
                categoryDTOS.add(addCategoryDTO);
                return categoryDTOS;
            }

            categoryDTOS = categories.stream()
                    .map(category1 -> {
                                AddCategoryDto addCategoryDTO = new AddCategoryDto();
                                addCategoryDTO.setId(category1.getId());
                                addCategoryDTO.setName(category1.getName());
                                return addCategoryDTO;
                            }
                    )
                    .toList();

            return categoryDTOS;

        }

        List<Category> categories = categoryRepository.findAllByParentCategoryIdIsNull();

        categoryDTOS = categories.stream()
                .map(
                        category -> {
                            AddCategoryDto addCategoryDTO = new AddCategoryDto();
                            addCategoryDTO.setName(category.getName());
                            addCategoryDTO.setId(category.getId());
                            return addCategoryDTO;
                        }
                )
                .toList();


        return categoryDTOS;
    }

    public FilteringCategoryDetailDTO getCategoryRelatedFilteringData(Long id) {
        FilteringCategoryDetailDTO categoryDetailDTO = new FilteringCategoryDetailDTO();

        List<Long> categoryIds = findAllLeafCategoryIds(id);
        List<CategoryMetadataFieldValue> fieldValues = new ArrayList<>();

        List<Product> products = new ArrayList<>();
        for (int i=0;i<categoryIds.size();i++){
            fieldValues.addAll(categoryMetadataFieldValueRepository.findByCategoryId(categoryIds.get(i)));
            products.addAll(productRepository.findByCategoryId(categoryIds.get(i)));
        }

        Map<String,String> metadata = new HashMap<>();

        for(CategoryMetadataFieldValue value : fieldValues){
            metadata.put(value.getMetadataField().getName(),value.getValue());
        }
        categoryDetailDTO.setMetadata(metadata);

        List<String> brands = new ArrayList<>();
        for (int i=0;i<products.size();i++){
            brands.add(products.get(i).getBrand());
        }
        categoryDetailDTO.setBrands(brands);
        categoryDetailDTO.setHighestPrice(productVariationRepository.findMaxPriceByProductIn(products));
        categoryDetailDTO.setLowestPrice(productVariationRepository.findMinPriceByProductIn(products));
        return categoryDetailDTO;

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
}
