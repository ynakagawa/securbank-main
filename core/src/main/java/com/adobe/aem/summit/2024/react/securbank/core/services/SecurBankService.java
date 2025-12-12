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

package com.adobe.aem.summit.2024.react.securbank.core.services;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Service interface for SecurBank business logic.
 */
@ProviderType
public interface SecurBankService {

    /**
     * Validates a bank account number.
     *
     * @param accountNumber the account number to validate
     * @return true if the account number is valid, false otherwise
     */
    boolean validateAccountNumber(String accountNumber);

    /**
     * Formats a bank account number for display.
     *
     * @param accountNumber the account number to format
     * @return formatted account number
     */
    String formatAccountNumber(String accountNumber);

    /**
     * Calculates interest for a given principal and rate.
     *
     * @param principal the principal amount
     * @param rate the interest rate (as a decimal, e.g., 0.05 for 5%)
     * @param years the number of years
     * @return the calculated interest
     */
    double calculateInterest(double principal, double rate, int years);
}

