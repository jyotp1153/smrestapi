package com.shyamSofttech.studentManagement.implementation;

import com.shyamSofttech.studentManagement.authUtils.JwtHelper;
import com.shyamSofttech.studentManagement.configs.AuthConfig;
import com.shyamSofttech.studentManagement.constant.ApiErrorCodes;
import com.shyamSofttech.studentManagement.constant.Status;
import com.shyamSofttech.studentManagement.dto.req.user.JwtRequest;
import com.shyamSofttech.studentManagement.dto.req.user.UserRequestDto;
import com.shyamSofttech.studentManagement.dto.req.user.UserUpdateRequestDto;
import com.shyamSofttech.studentManagement.dto.res.user.UserResponse;
import com.shyamSofttech.studentManagement.dto.res.util.PaginatedResp;
import com.shyamSofttech.studentManagement.email.EmailOtpService;
import com.shyamSofttech.studentManagement.email.EmailService;
import com.shyamSofttech.studentManagement.entities.UserDetailsEntity;
import com.shyamSofttech.studentManagement.entities.UserEntity;
import com.shyamSofttech.studentManagement.exception.NoSuchElementFoundException;
import com.shyamSofttech.studentManagement.exception.ValidationException;
import com.shyamSofttech.studentManagement.exception.UserAlreadyExistsException;
import com.shyamSofttech.studentManagement.repositories.UserRepo;
import com.shyamSofttech.studentManagement.service.AuthService;
import com.shyamSofttech.studentManagement.util.ImageUploader;
import com.shyamSofttech.studentManagement.util.MapperUtil;
import com.shyamSofttech.studentManagement.util.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;

    private final EmailService emailService;

    private final ImageUploader imageUploader;

    private final AuthConfig authConfig;

    private final JwtHelper jwtHelper;

    private final EmailOtpService emailOtpService;

    private final MapperUtil mapperUtil;


    @Override
    @Transactional
    public String createUser(UserRequestDto userRequestDto) {
        log.info("Attempting to create a new user with mobile: {} and email: {}", userRequestDto.getMobile(), userRequestDto.getEmail());
        Optional<UserEntity> foundUser = userRepo.findByMobileNo(userRequestDto.getMobile());
        Optional<UserEntity> foundUserWithEmail = userRepo.findByEmail(userRequestDto.getEmail());
        if (foundUser.isPresent() || foundUserWithEmail.isPresent()) {
            log.warn("User already exists with provided mobile or email.");
            throw new UserAlreadyExistsException(ApiErrorCodes.USER_ALREADY_EXIST.getErrorCode(), ApiErrorCodes.USER_ALREADY_EXIST.getErrorMessage());
        }
        if (Validator.isValidMobileNo(userRequestDto.getMobile())) {
            log.warn("Invalid mobile number: {}", userRequestDto.getMobile());
            throw new ValidationException(ApiErrorCodes.INVALID_MOBILE_NUMBER.getErrorCode(), ApiErrorCodes.INVALID_MOBILE_NUMBER.getErrorMessage());
        }
        if (userRequestDto.getEmail() != null && Validator.isValidEmail(userRequestDto.getEmail())) {
            log.warn("Invalid email address: {}", userRequestDto.getEmail());
            throw new ValidationException(ApiErrorCodes.INVALID_EMAIL.getErrorCode(), ApiErrorCodes.INVALID_EMAIL.getErrorMessage());
        }
        UserEntity user = this.mapDtoToEntity(userRequestDto);
        userRepo.save(user);
        log.info("User created successfully with name: {}", user.getName());
        UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
        userDetailsEntity.setMobileNo(user.getMobileNo());
        return jwtHelper.generateToken(userDetailsEntity);
    }

    @Override
    public UserResponse getUserById(Long userID) {
        log.info("Fetching user with ID: {}", userID);
        Optional<UserEntity> optionalUserEntity = userRepo.findById(userID);
        if (optionalUserEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.USER_NOT_FOUND.getErrorCode(), ApiErrorCodes.USER_NOT_FOUND.getErrorMessage());
        }
        log.info("User with ID: {} found. Mapping to JwtResponse.", userID);
        return mapperUtil.mapToUserRes(optionalUserEntity.get());
    }

    @Override
    public void deleteAccount(Long id) {
        log.info("Attempting to delete account with ID: {}", id);
        Optional<UserEntity> optionalUserEntity = userRepo.findById(id);
        if (optionalUserEntity.isEmpty()) {
            log.warn("User not found for ID: {}", id);
            throw new NoSuchElementFoundException(ApiErrorCodes.USER_NOT_FOUND.getErrorCode(), ApiErrorCodes.USER_NOT_FOUND.getErrorMessage());
        }
        optionalUserEntity.get().setStatus(Status.INACTIVE);
        userRepo.save(optionalUserEntity.get());
        String subject = "Account Deletion Notification";
        String messageBody = "Dear " + optionalUserEntity.get().getName() + ",\n\n" + "Your account has been successfully deleted. ";
        log.info("User account deactivated for ID: {}", id);
        emailService.sendEmail(optionalUserEntity.get().getEmail(), subject, messageBody);

    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequestDto userUpdateRequestDto) {
        log.info("Attempting to update user with ID: {}", id);
        Optional<UserEntity> optionalUserEntity = userRepo.findById(id);
        if (optionalUserEntity.isPresent()) {
            UserEntity user = optionalUserEntity.get();
            updateEntityFromDto(user, userUpdateRequestDto);
            log.info("User details updated for ID: {}", id);
            return mapperUtil.mapToUserRes(userRepo.save(user));
        }
        log.warn("User not found for ID: {}", id);
        throw new NoSuchElementFoundException(ApiErrorCodes.USER_NOT_FOUND.getErrorCode(), ApiErrorCodes.USER_NOT_FOUND.getErrorMessage());
    }

    @Override
    public UserDetailsEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        Optional<UserEntity> userDetail = userRepo.findByMobileNo(Long.valueOf(username));
        if (userDetail.isEmpty()) {
            log.warn("Invalid username or password for username: {}", username);
            throw new NoSuchElementFoundException(ApiErrorCodes.INVALID_USERNAME_OR_PASSWORD.getErrorCode(), ApiErrorCodes.INVALID_USERNAME_OR_PASSWORD.getErrorMessage());
        }
        return mapToUserDetails(userDetail.get());
    }

    @Override
    public String userLogin(JwtRequest request) {
        log.info("Attempting user login with mobile: {}", request.getMobileNo());
        UserDetails userDetails = loadUserByUsername(String.valueOf(request.getMobileNo()));
        if (authConfig.matches(request.getPassword(), userDetails.getPassword())) {
            log.info("Login successful for mobile: {}", request.getMobileNo());
            return jwtHelper.generateToken(userDetails);
        }
        log.warn("Invalid login attempt for mobile: {}", request.getMobileNo());
        throw new ValidationException(ApiErrorCodes.INVALID_USERNAME_OR_PASSWORD.getErrorCode(), ApiErrorCodes.INVALID_USERNAME_OR_PASSWORD.getErrorMessage());
    }

    @Override
    public String resetPassword(Long id, String newPassword) {
        log.info("Initiating password reset for user ID: {}", id);
        Optional<UserEntity> optionalUserEntity = userRepo.findById(id);
        if (optionalUserEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.USER_NOT_FOUND.getErrorCode(), ApiErrorCodes.USER_NOT_FOUND.getErrorMessage());
        }
        UserEntity userEntity = optionalUserEntity.get();
        userEntity.setPassword(authConfig.passwordEncoder().encode(newPassword));
        userRepo.save(userEntity);
        log.info("Password updated for user ID: {}. Sending notification email to {}", id, userEntity.getEmail());
        emailService.sendPasswordResetEmail(userEntity.getEmail(), userEntity.getName());
        log.info("Password reset completed for user ID: {}", id);
        return "password changed";
    }

    public UserEntity mapDtoToEntity(UserRequestDto userReqDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userReqDto.getEmail());
        if (!Objects.equals(userReqDto.getUserPhotoUrl(), "")) {
            userEntity.setUserPhotoUrl(imageUploader.uploadFile(userReqDto.getUserPhotoUrl(), "jpg"));
        }
        userEntity.setMobileNo(userReqDto.getMobile());
        userEntity.setPassword(authConfig.passwordEncoder().encode(userReqDto.getPassword()));
        userEntity.setName(userReqDto.getName());
        userEntity.setStatus(Status.ACTIVE);
        if (CollectionUtils.isEmpty(userReqDto.getUserType())) {
            throw new ValidationException(ApiErrorCodes.ROLE_LIST_NOT_PRESENT.getErrorCode(), ApiErrorCodes.ROLE_LIST_NOT_PRESENT.getErrorMessage());
        }
        userEntity.setUserRoleList(userReqDto.getUserType());
        userEntity.setGender(userReqDto.getGender());
        return userEntity;
    }

    public UserDetailsEntity mapToUserDetails(UserEntity user) {
        UserDetailsEntity userDetails = new UserDetailsEntity();
        userDetails.setMobileNo(user.getMobileNo());
        userDetails.setPassword(user.getPassword());
        return userDetails;
    }

    public void updateEntityFromDto(UserEntity userEntity, UserUpdateRequestDto userUpdateRequestDto) {
        userEntity.setEmail(userUpdateRequestDto.getEmail());
        userEntity.setMobileNo(userUpdateRequestDto.getMobileNo());
        if (!Objects.equals(userUpdateRequestDto.getUserPhotoUrl(), "")) {
            userEntity.setUserPhotoUrl(imageUploader.uploadFile(userUpdateRequestDto.getUserPhotoUrl(), "jpg"));
        }
        userEntity.setName(userUpdateRequestDto.getName());
    }

    @Override
    public String verifyUserEmailOtp(String email, String otp) {
        boolean isValid = emailOtpService.verifyEmailCode(email, otp);
        if (!isValid) {
            throw new NoSuchElementFoundException(ApiErrorCodes.INVALID_EMAIL_CODE.getErrorCode(), ApiErrorCodes.INVALID_EMAIL_CODE.getErrorMessage());
        }
        log.info("Email OTP verified successfully for email: {}", email);
        return "Done";
    }

    @Override
    public String sendOtpToEmail(String email) {
        log.info("Request received to send OTP to email: {}", email);
        Optional<UserEntity> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.USER_ALREADY_EXIST.getErrorCode(), ApiErrorCodes.USER_ALREADY_EXIST.getErrorMessage());
        }
        emailOtpService.sendEmailCode(email);
        log.info("OTP sent successfully to email: {}", email);
        return "otp send";
    }

    @Override
    public PaginatedResp<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching users - Page: {}, Size: {}, SortBy: {}, SortDirection: {}", page, size, sortBy, sortDirection);
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserEntity> userEntityPage = userRepo.findAll(pageable);
        log.info("Fetched {} users out of total {} users (Total Pages: {})", userEntityPage.getNumberOfElements(), userEntityPage.getTotalElements(), userEntityPage.getTotalPages());
        List<UserResponse> jwtResponseList = new ArrayList<>();
        jwtResponseList = userEntityPage.getContent().stream().map(mapperUtil::mapToUserRes).toList();
        return new PaginatedResp<>(userEntityPage.getTotalElements(), userEntityPage.getTotalPages(), page, jwtResponseList);
    }

}

