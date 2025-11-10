package com.shyamSofttech.studentManagement.util;


public class Validator {
    public static boolean isValidMobileNo(Long mobileNo){
        return mobileNo == null || mobileNo.toString().length() != 10;
    }
    public static boolean isValidEmail(String email){
        return email == null || !email.contains("@") || !email.contains(".");
    }
    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
