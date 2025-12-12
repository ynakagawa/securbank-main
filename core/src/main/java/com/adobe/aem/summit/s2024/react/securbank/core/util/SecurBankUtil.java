/*
 * SecurBank Core
 *
 * Copyright (C) 2024 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.adobe.aem.summit.s2024.react.securbank.core.util;

/**
 * Utility class for SecurBank operations.
 */
public final class SecurBankUtil {

    private SecurBankUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Masks sensitive account information for display.
     *
     * @param accountNumber the account number to mask
     * @return masked account number (e.g., "****-****-1234")
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return "";
        }

        String cleaned = accountNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() <= 4) {
            return "****";
        }

        int visibleDigits = Math.min(4, cleaned.length());
        String visible = cleaned.substring(cleaned.length() - visibleDigits);
        int maskedLength = cleaned.length() - visibleDigits;

        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < maskedLength; i++) {
            masked.append("*");
        }
        masked.append(visible);

        // Add dashes for readability
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < masked.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append("-");
            }
            formatted.append(masked.charAt(i));
        }

        return formatted.toString();
    }

    /**
     * Validates if a string is a valid email format.
     *
     * @param email the email to validate
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String trimmed = email.trim();
        // Simple email validation - must have @, dot after @, and minimum length
        int atIndex = trimmed.indexOf("@");
        return atIndex > 0 && trimmed.contains(".") && trimmed.lastIndexOf(".") > atIndex && trimmed.length() >= 5;
    }

    /**
     * Formats a currency amount for display.
     *
     * @param amount the amount to format
     * @return formatted currency string (e.g., "$1,234.56")
     */
    public static String formatCurrency(double amount) {
        if (amount < 0) {
            return "-$" + String.format("%.2f", Math.abs(amount));
        }
        return "$" + String.format("%.2f", amount);
    }
}

