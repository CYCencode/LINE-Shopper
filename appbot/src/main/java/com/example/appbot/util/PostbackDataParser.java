package com.example.appbot.util;

import java.util.HashMap;
import java.util.Map;

public class PostbackDataParser {

    public static Map<String, String> parse(String data) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = data.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result;
    }
}
