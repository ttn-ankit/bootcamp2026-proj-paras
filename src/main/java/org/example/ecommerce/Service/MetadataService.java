package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.AddMetaDataFieldValueDto;
import org.example.ecommerce.DTOS.Request.CategoryMetadataFieldDto;
import org.example.ecommerce.DTOS.Response.BasicResponse;
import org.example.ecommerce.DTOS.Response.GetACategoryDTO;
import org.example.ecommerce.DTOS.Response.GetCategoryMetadataFieldValueBySellerDTO;
import org.example.ecommerce.Entity.Category;
import org.example.ecommerce.Entity.CategoryMetadataField;
import org.example.ecommerce.Entity.CategoryMetadataFieldValue;
import org.example.ecommerce.Entity.CategoryMetadataFieldValuesKey;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Repository.CategoryMetadataFieldRepository;
import org.example.ecommerce.Repository.CategoryMetadataFieldValueRepository;
import org.example.ecommerce.Repository.CategoryRepository;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MetadataService {

    CategoryMetadataFieldRepository repository;
    MessageSource messageSource;
    CategoryRepository categoryRepository;
    CategoryMetadataFieldValueRepository valueRepository;

    public BasicResponse addFieldName(String name, Locale locale) {
        if(repository.existsByName(name)){

            String message = messageSource.getMessage("field.already.exists", null, locale);
            throw new APIException(message, HttpStatus.BAD_REQUEST);

        }

        CategoryMetadataField field = new CategoryMetadataField();
        field.setName(name.toLowerCase());
        CategoryMetadataField myField = repository.save(field);
        Object[] ob = new Object[]{name,myField.getId()};
        return new BasicResponse(messageSource.getMessage("message.new.metadata.field.added",ob,locale),200);
    }


    public List<CategoryMetadataFieldDto> getAllMetadataFields(Integer pageSize, Integer offset, String sort, String order, String query) {

        Pageable pageable = PageRequest.of(offset,pageSize, Sort.by(Sort.Direction.fromString(order),sort));

        DynamicSpecification<CategoryMetadataField> dynamicSpecification = new DynamicSpecification<>();
        Specification<CategoryMetadataField> spec = dynamicSpecification.build(query);

        Page<CategoryMetadataField> page;
        if (spec != null) {
            page = repository.findAll(spec, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        List<CategoryMetadataField> fields = page.getContent();

        return fields.stream()
                .map(field->{
                    CategoryMetadataFieldDto fieldDTO = new CategoryMetadataFieldDto();
                    fieldDTO.setId(field.getId());
                    fieldDTO.setName(field.getName());
                    return fieldDTO;
                })
                .toList();
    }


    public BasicResponse addMetadataCategoryFieldValues(AddMetaDataFieldValueDto metadataFieldValueDTO, Locale locale) {


        Set<String> values = metadataFieldValueDTO.getValues();

        if (values.isEmpty()){
            String localizedMessage = messageSource.getMessage("values.must.be.less.than.one", null, locale);
            throw new APIException(localizedMessage, HttpStatus.BAD_REQUEST);
        }


        Pattern regex = Pattern.compile("^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*$");
        for (String value : values) {
            if (!regex.matcher(value).matches()) {
                throw new APIException("Invalid pattern in values: " + value,HttpStatus.BAD_REQUEST);
            }
        }


        Category category = categoryRepository.findById(metadataFieldValueDTO.getCategoryId()).orElseThrow(
                ()-> {String localizedMessage = messageSource.getMessage("category.id.not.found", new Object[]{metadataFieldValueDTO.getCategoryId()}, locale);
                    throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);}

        );

        CategoryMetadataField metadataField = repository.findById(metadataFieldValueDTO.getMetadataFieldId())
                .orElseThrow(()-> {
                    String localizedMessage = messageSource.getMessage("metadata.category.id.not.found", new Object[]{metadataFieldValueDTO.getMetadataFieldId()}, locale);
                    throw new APIException(localizedMessage, HttpStatus.BAD_REQUEST);
                });


        if(categoryRepository.existsByParentCategoryId(metadataFieldValueDTO.getCategoryId())){
            String localizedMessage = messageSource.getMessage("parent.category.metadata.not.allowed", null, locale);
            throw new APIException(localizedMessage, HttpStatus.BAD_REQUEST);
        }

        CategoryMetadataFieldValuesKey key = new CategoryMetadataFieldValuesKey(metadataFieldValueDTO.getCategoryId(),metadataFieldValueDTO.getMetadataFieldId());

        boolean exists = valueRepository.existsById(key);

        if (exists) {
            String localizedMessage = messageSource.getMessage("metadata.field.already.defined", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        CategoryMetadataFieldValue categoryMetadataFieldValues = new CategoryMetadataFieldValue();
        categoryMetadataFieldValues.setCategory(category);
        categoryMetadataFieldValues.setMetadataField(metadataField);
        categoryMetadataFieldValues.setId(key);
        String csv = String.join(",", metadataFieldValueDTO.getValues().stream().map(value-> value.toLowerCase()).toList());
        categoryMetadataFieldValues.setValue(csv);

        valueRepository.save(categoryMetadataFieldValues);
        String response = messageSource.getMessage("message.metadata.fieldadded",null,locale);
        return new BasicResponse(response,200);
    }

    public List<GetCategoryMetadataFieldValueBySellerDTO> GetCategoryAndMetadataValue(Locale locale) {

        List<Category> categories = categoryRepository.findAllNotInParentCategory();
        if (categories.isEmpty()) {
            String localizedMessage = messageSource.getMessage("no.leaf.category.found", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        List<CategoryMetadataFieldValue> values = valueRepository.findAll();

        List<GetCategoryMetadataFieldValueBySellerDTO> dtos = new ArrayList<>();

        for (Category category : categories) {
            List<CategoryMetadataFieldValue> categoryValues = values.stream()
                    .filter(val -> val.getCategory().getId().equals(category.getId()))
                    .toList();

            if (categoryValues.isEmpty()) {
                GetCategoryMetadataFieldValueBySellerDTO dto = new GetCategoryMetadataFieldValueBySellerDTO();
                dto.setCategoryName(category.getName());
                dto.setFieldName(null);
                dto.setFieldValues("");
                List<GetACategoryDTO> parentChain = new ArrayList<>();
                Category parent = category.getParentCategory();

                while (parent != null) {
                    GetACategoryDTO getACategoryDTO = new GetACategoryDTO();
                    getACategoryDTO.setId(parent.getId());
                    getACategoryDTO.setName(parent.getName());
                    parentChain.add(getACategoryDTO);
                    parent = parent.getParentCategory();
                }
                dto.setParent(parentChain);
                dtos.add(dto);
            } else {
                for (CategoryMetadataFieldValue value : categoryValues) {
                    GetCategoryMetadataFieldValueBySellerDTO dto = new GetCategoryMetadataFieldValueBySellerDTO();
                    dto.setCategoryName(category.getName());
                    dto.setFieldName(value.getMetadataField().getName());
                    dto.setFieldValues(value.getValue());
                    List<GetACategoryDTO> parentChain = new ArrayList<>();
                    Category parent = category.getParentCategory();

                    while (parent != null) {
                        GetACategoryDTO getACategoryDTO = new GetACategoryDTO();
                        getACategoryDTO.setId(parent.getId());
                        getACategoryDTO.setName(parent.getName());
                        parentChain.add(getACategoryDTO);
                        parent = parent.getParentCategory();
                    }
                    dto.setParent(parentChain);
                    dtos.add(dto);
                }
            }

        }

        return dtos;
    }


    public BasicResponse updateMetadataValues(AddMetaDataFieldValueDto metadataFieldValueDTO, Locale locale) {

        Category category = categoryRepository.findById(metadataFieldValueDTO.getCategoryId()).orElseThrow(
                ()-> {
                    String localizedMessage = messageSource.getMessage("category.id.not.found", new Object[]{metadataFieldValueDTO.getCategoryId()}, locale);
                    throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);
                }
        );

        CategoryMetadataField metadataField = repository.findById(metadataFieldValueDTO.getMetadataFieldId())
                .orElseThrow(()-> {String localizedMessage = messageSource.getMessage("metadata.category.id.not.found", new Object[]{metadataFieldValueDTO.getMetadataFieldId()}, locale);
                    throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);
                });

        Set<String> values = metadataFieldValueDTO.getValues();

        if (values.isEmpty()){
            String localizedMessage = messageSource.getMessage("values.must.be.less.than.one", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        CategoryMetadataFieldValuesKey key = new CategoryMetadataFieldValuesKey(metadataFieldValueDTO.getCategoryId(),metadataFieldValueDTO.getMetadataFieldId());

        boolean exists = valueRepository.existsById(key);

        if (!exists) {
            String localizedMessage = messageSource.getMessage("metadata.field.not.defined.for.category", null, locale);
            throw new APIException(localizedMessage,HttpStatus.BAD_REQUEST);

        }

        CategoryMetadataFieldValue values1 = valueRepository.findById(key).orElseThrow(
                ()->{String localizedMessage = messageSource.getMessage("values.field.not.found", null, locale);
                    throw new APIException(localizedMessage, HttpStatus.BAD_REQUEST);
                }
        );

        Set<String> valueSet = new HashSet<>(Arrays.asList(values1.getValue().toLowerCase().split(",")));
        valueSet.addAll(values);

        values1.setValue(String.join(",",valueSet));

        valueRepository.save(values1);
        String response = messageSource.getMessage("message.metadata.fieldvalueadded",null,locale);
        return new BasicResponse(response,200);
    }
}
