package com.example.appbot.util;

import java.util.UUID;

public class FileUtil {
    public static String generateUuidFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        return UUID.randomUUID().toString() + fileExtension;
    }
}
