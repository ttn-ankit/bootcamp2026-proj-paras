package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddMetaDataFieldValueDto {

    @NotNull(message = "must provide category id")
     Long categoryId;
    @NotNull(message = "must provide category metadata field id")
     Long metadataFieldId;

    @NotNull(message = "values should not be null")
    @Size(min = 1, message = "values should not be empty")
     Set<String> values;
}
