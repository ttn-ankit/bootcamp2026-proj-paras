package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordDto {
    @NotBlank(message = "password must not be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,15}$",
            message ="Password should contain 8-15 Characters with least 1 Lower case, 1 Upper case, 1 Special Character, 1 Number")
     String password;

    @NotBlank(message = "confirm password must not be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,15}$",
            message ="Password should contain 8-15 Characters with least 1 Lower case, 1 Upper case, 1 Special Character, 1 Number")
     String confirmPassword;
}
