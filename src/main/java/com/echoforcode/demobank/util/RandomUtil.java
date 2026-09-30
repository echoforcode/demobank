package com.echoforcode.demobank.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
@Component
public class RandomUtil {
    private final SecureRandom random = new SecureRandom();
    public long generateRandom(int length) {
        if (length < 1 || length > 18) {
            throw new IllegalArgumentException("Length must be 1 to 18");
        }
        long number = 1 + random.nextInt(9);
        for (int i = 1; i < length; i++) {
            number = number * 10 + random.nextInt(10);
        }
        return number;
    }
}
