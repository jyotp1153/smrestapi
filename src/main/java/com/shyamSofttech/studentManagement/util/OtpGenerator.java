package com.shyamSofttech.studentManagement.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class OtpGenerator {

    private static final String OTP_CHARS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(OTP_CHARS.charAt(RANDOM.nextInt(OTP_CHARS.length())));
        }
        return otp.toString();
    }
}
