package com.bookstore.utils;

import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class Base64EncoderUtil {

    public String encodeToString(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}