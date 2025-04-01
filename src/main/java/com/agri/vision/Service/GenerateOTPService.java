package com.agri.vision.Service;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;

@Service
public class GenerateOTPService {

    private static final String NUMERIC_STRING = "0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(NUMERIC_STRING.length());
            otp.append(NUMERIC_STRING.charAt(index));
        }

        return otp.toString();
    }
}
