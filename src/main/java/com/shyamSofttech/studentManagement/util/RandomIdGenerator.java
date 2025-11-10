package com.shyamSofttech.studentManagement.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;


@UtilityClass
public class RandomIdGenerator {

    private static final String NUMERIC = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateIdInNumber() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 11; i++) {
            sb.append(NUMERIC.charAt(RANDOM.nextInt(NUMERIC.length()))) ;
        }
        return sb.toString();
    }

}
