package com.library.util;

import java.security.SecureRandom;
import java.util.Base64;

public class OpaqueUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateToken() {
        byte[] bytes = new byte[32]; // 256 bit
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
