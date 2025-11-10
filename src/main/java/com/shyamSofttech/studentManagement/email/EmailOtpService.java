package com.shyamSofttech.studentManagement.email;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class EmailOtpService {
    @Autowired
    private EmailService emailService;
    private static final String OTP_CHARS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Cache<String, String> emailOtpCache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .build();

    public void sendEmailCode(String email){
        String emailOtp = generateOtp(4);
        emailService.sendEmailOtp(emailOtp, email);
        emailOtpCache.put(email, emailOtp);
        System.out.println("code sent to email"+email);
    }

    public boolean verifyEmailCode(String email, String emailCode) {
        String cachedOtp = emailOtpCache.getIfPresent(email);
        return cachedOtp != null && cachedOtp.equals(emailCode);
    }

    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(OTP_CHARS.charAt(RANDOM.nextInt(OTP_CHARS.length())));
        }
        return otp.toString();
    }
}
