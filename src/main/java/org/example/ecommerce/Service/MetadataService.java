package org.example.ecommerce.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.ecommerce.DTOS.Request.CategoryMetadataFieldDto;
import org.example.ecommerce.Entity.CategoryMetadataField;
import org.example.ecommerce.Repository.CategoryMetadataFieldRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MetadataService {

    CategoryMetadataFieldRepository repository;

    public CategoryMetadataFieldDto addMetadataField(CategoryMetadataFieldDto dto) {

        if (repository.existsByName(dto.getName())) {
            throw new RuntimeException("Already Exist");
        } else {
            CategoryMetadataField metadataField = new CategoryMetadataField();
            metadataField.setName(dto.getName());
            repository.save(metadataField);
            return new CategoryMetadataFieldDto("Added Successfully", dto.getId());
        }
    }

    public List<CategoryMetadataFieldDto> getAllMetadataFields(Integer max,Integer offset,String sort,String order,String query){


        int pageNumber = offset/max;
        Pageable pageable = PageRequest.of( pageNumber,max, Sort.by(sort));

//        Page<CategoryMetadataField> allField = repository.findAll(,pageable);
        return null;

    }
}
