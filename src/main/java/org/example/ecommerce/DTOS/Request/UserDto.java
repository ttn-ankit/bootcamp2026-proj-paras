package org.example.ecommerce.DTOS.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @Email(message = "Invalid Email")
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",message = "Invalid email format")
     String email;

    @Pattern(regexp = "^(?=(?:.*[A-Za-z]){3,})[A-Za-z]+$",message = "enter valid first name")
    @NotBlank(message = "First Name should not be Blank")
     String firstName;
     String middleName;

    @Pattern(regexp = "^(?=(?:.*[A-Za-z]){3,})[A-Za-z]+$",message = "enter valid last name")
    @NotBlank(message = "Last Name should not be Blank")
     String lastName;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,15}$",
            message ="Password should contain 8-15 Characters with least 1 Lower case, 1 Upper case, 1 Special Character, 1 Number")
     String password;

    @NotBlank(message = "confirm password must not be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,15}$",
            message ="Password should contain 8-15 Characters with least 1 Lower case, 1 Upper case, 1 Special Character, 1 Number")
     String confirmPassword;
}
