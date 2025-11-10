package com.shyamSofttech.studentManagement.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final String ASCENDING = "ASC";
    public final String DESCENDING = "DESC";
    public final String GENDER_REGEX = "^(?i)(male|female|others)$";
    public final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdTime";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
}
