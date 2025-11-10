package com.shyamSofttech.studentManagement.entities;

import com.shyamSofttech.studentManagement.constant.Status;
import com.shyamSofttech.studentManagement.constant.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {
    String name;
    String email;
    String userName;
    String password;
    String gender;
    String userPhotoUrl;
    Long mobileNo;
    @Enumerated(EnumType.STRING)
    List<UserRole> userRoleList;
    @Enumerated(EnumType.STRING)
    Status status;
}
