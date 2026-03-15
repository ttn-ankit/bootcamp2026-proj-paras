package org.example.ecommerce.Entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class CategoryMetadataFieldValuesKey implements Serializable {

     Long categoryId;
     Long categoryMetaDataFieldId;

}