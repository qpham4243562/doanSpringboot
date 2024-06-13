package com.bookstore.utils;

import java.util.Base64;

public class ImageUtils {

    public static String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}