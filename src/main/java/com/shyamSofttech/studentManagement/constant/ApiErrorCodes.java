package com.shyamSofttech.studentManagement.constant;

import lombok.Getter;

@Getter
public enum ApiErrorCodes implements Error {

    INVALID_INPUT(10001, "Invalid request input"),
    USER_NOT_FOUND(10005, "User not found"),
    INVALID_USERNAME_OR_PASSWORD(10006, "Invalid username or password"),
    USER_ALREADY_EXIST(10007, "User already exist"),
    INVALID_EMAIL(10009, "Invalid email"),
    ERROR_WHILE_SENDING_EMAIL(10010, "Error while sending email"),
    ROLE_LIST_NOT_PRESENT(10011, "Role list not present"),
    INVALID_EMAIL_CODE(10012, "Invalid email code"),
    INVALID_MOBILE_NUMBER(10008, "Invalid mobile number");

    private final int errorCode;
    private final String errorMessage;

    ApiErrorCodes(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}

