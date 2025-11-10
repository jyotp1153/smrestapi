package com.shyamSofttech.studentManagement.exception;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);

    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }}

