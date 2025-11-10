package com.shyamSofttech.studentManagement.dto.req.user;

import com.shyamSofttech.studentManagement.constant.Constants;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequestDto {
    String name;
    String userName;
    String userPhotoUrl;
    Long mobileNo;
    @Pattern(regexp = Constants.EMAIL_REGEX, message = "Invalid email, please enter valid email")
    String email;
    String password;
}
