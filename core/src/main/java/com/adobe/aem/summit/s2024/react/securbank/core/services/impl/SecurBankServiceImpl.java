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

package com.adobe.aem.summit.s2024.react.securbank.core.services.impl;

import com.adobe.aem.summit.s2024.react.securbank.core.services.SecurBankService;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of SecurBankService.
 */
@Component(service = SecurBankService.class)
public class SecurBankServiceImpl implements SecurBankService {

    private static final int MIN_ACCOUNT_LENGTH = 8;
    private static final int MAX_ACCOUNT_LENGTH = 16;

    @Override
    public boolean validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return false;
        }

        String cleaned = accountNumber.replaceAll("[^0-9]", "");
        return cleaned.length() >= MIN_ACCOUNT_LENGTH && cleaned.length() <= MAX_ACCOUNT_LENGTH;
    }

    @Override
    public String formatAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return "";
        }

        String cleaned = accountNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() < 4) {
            return cleaned;
        }

        // Format as XXXX-XXXX-XXXX-XXXX
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cleaned.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append("-");
            }
            formatted.append(cleaned.charAt(i));
        }
        return formatted.toString();
    }

    @Override
    public double calculateInterest(double principal, double rate, int years) {
        if (principal < 0 || rate < 0 || years < 0) {
            throw new IllegalArgumentException("Principal, rate, and years must be non-negative");
        }

        // Simple interest calculation: I = P * r * t
        return principal * rate * years;
    }
}

