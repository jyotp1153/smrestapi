package com.shyamSofttech.studentManagement.util;

import com.shyamSofttech.studentManagement.dto.res.user.UserResponse;
import com.shyamSofttech.studentManagement.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapperUtil {
    public UserResponse mapToUserRes(UserEntity user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setStatus(user.getStatus());
        userResponse.setName(user.getName());
        userResponse.setUserName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setId(user.getId());
        return userResponse;
    }
}
