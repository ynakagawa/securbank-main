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

package com.adobe.aem.summit.2024.react.securbank.core.services.impl;

import com.adobe.aem.summit.2024.react.securbank.core.services.SecurBankService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SecurBankServiceImplTest {

    private SecurBankService securBankService;

    @Before
    public void setUp() {
        securBankService = new SecurBankServiceImpl();
    }

    @Test
    public void testValidateAccountNumber_Valid() {
        assertTrue(securBankService.validateAccountNumber("12345678"));
        assertTrue(securBankService.validateAccountNumber("1234567890123456"));
        assertTrue(securBankService.validateAccountNumber("1234-5678-9012-3456"));
    }

    @Test
    public void testValidateAccountNumber_Invalid() {
        assertFalse(securBankService.validateAccountNumber(null));
        assertFalse(securBankService.validateAccountNumber(""));
        assertFalse(securBankService.validateAccountNumber("   "));
        assertFalse(securBankService.validateAccountNumber("1234567")); // too short
        assertFalse(securBankService.validateAccountNumber("12345678901234567")); // too long
    }

    @Test
    public void testFormatAccountNumber_Valid() {
        assertEquals("1234-5678-9012-3456", securBankService.formatAccountNumber("1234567890123456"));
        assertEquals("1234-5678", securBankService.formatAccountNumber("12345678"));
        assertEquals("1234-5678-9012", securBankService.formatAccountNumber("123456789012"));
    }

    @Test
    public void testFormatAccountNumber_WithDashes() {
        assertEquals("1234-5678-9012-3456", securBankService.formatAccountNumber("1234-5678-9012-3456"));
    }

    @Test
    public void testFormatAccountNumber_Short() {
        assertEquals("123", securBankService.formatAccountNumber("123"));
        assertEquals("", securBankService.formatAccountNumber(""));
    }

    @Test
    public void testFormatAccountNumber_Null() {
        assertEquals("", securBankService.formatAccountNumber(null));
    }

    @Test
    public void testCalculateInterest_Valid() {
        assertEquals(100.0, securBankService.calculateInterest(1000.0, 0.05, 2), 0.01);
        assertEquals(0.0, securBankService.calculateInterest(1000.0, 0.0, 2), 0.01);
        assertEquals(0.0, securBankService.calculateInterest(0.0, 0.05, 2), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateInterest_NegativePrincipal() {
        securBankService.calculateInterest(-1000.0, 0.05, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateInterest_NegativeRate() {
        securBankService.calculateInterest(1000.0, -0.05, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateInterest_NegativeYears() {
        securBankService.calculateInterest(1000.0, 0.05, -2);
    }
}

