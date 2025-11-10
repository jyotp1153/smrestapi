package com.shyamSofttech.studentManagement.dto.req.user;

import com.shyamSofttech.studentManagement.constant.Constants;
import com.shyamSofttech.studentManagement.constant.UserRole;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDto {
    String name;

    @Pattern(regexp = Constants.EMAIL_REGEX, message = "Invalid email, please enter valid email")
    String email;

    String password;

    List<UserRole> userType;

    Long mobile;

    String userPhotoUrl;
    String gender;



}
