package com.shyamSofttech.studentManagement.interceptor;

import com.shyamSofttech.studentManagement.constant.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserAuthorization {
    UserRole[] allowedRoles();
}