package com.shyamSofttech.studentManagement.service;

import com.shyamSofttech.studentManagement.dto.req.user.JwtRequest;
import com.shyamSofttech.studentManagement.dto.res.user.UserResponse;
import com.shyamSofttech.studentManagement.dto.req.user.UserRequestDto;
import com.shyamSofttech.studentManagement.dto.req.user.UserUpdateRequestDto;
import com.shyamSofttech.studentManagement.dto.res.util.PaginatedResp;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AuthService extends UserDetailsService{
    String createUser(UserRequestDto userRequestDto);
    UserResponse getUserById(Long userID);
    void deleteAccount(Long id);

    UserResponse updateUser(Long id, UserUpdateRequestDto userUpdateRequestDto);

    String userLogin(JwtRequest request);

    String resetPassword(Long id, String newPassword);

    String verifyUserEmailOtp(String email, String otp);

    String sendOtpToEmail(String email);

    PaginatedResp<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection);
}