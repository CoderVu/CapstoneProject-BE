package com.example.CapstoneProject.utils;

import java.text.Normalizer;
import java.time.format.DateTimeFormatter;

public class StringUtils {
    public static String normalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        // Loại bỏ dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay thế khoảng trắng bằng dấu gạch dưới
        normalized = normalized.replaceAll("\\s+", "_");

        // Loại bỏ ký tự không mong muốn (nếu có)
        normalized = normalized.replaceAll("[^a-zA-Z0-9_]", "");

        // Chuyển tất cả sang chữ thường
        return normalized.toLowerCase();
    }
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
}
