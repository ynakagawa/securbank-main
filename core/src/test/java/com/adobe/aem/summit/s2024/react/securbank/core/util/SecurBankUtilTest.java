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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SecurBankUtilTest {

    @Test
    public void testMaskAccountNumber_Valid() {
        String result = SecurBankUtil.maskAccountNumber("1234567890123456");
        assertTrue(result.contains("3456"));
        assertTrue(result.contains("*"));
    }

    @Test
    public void testMaskAccountNumber_Short() {
        assertEquals("****", SecurBankUtil.maskAccountNumber("1234"));
        assertEquals("****", SecurBankUtil.maskAccountNumber("12"));
    }

    @Test
    public void testMaskAccountNumber_Null() {
        assertEquals("", SecurBankUtil.maskAccountNumber(null));
    }

    @Test
    public void testMaskAccountNumber_Empty() {
        assertEquals("", SecurBankUtil.maskAccountNumber(""));
        assertEquals("", SecurBankUtil.maskAccountNumber("   "));
    }

    @Test
    public void testMaskAccountNumber_WithDashes() {
        String result = SecurBankUtil.maskAccountNumber("1234-5678-9012-3456");
        assertTrue(result.contains("3456"));
    }

    @Test
    public void testIsValidEmail_Valid() {
        assertTrue(SecurBankUtil.isValidEmail("test@example.com"));
        assertTrue(SecurBankUtil.isValidEmail("user.name@domain.co.uk"));
    }

    @Test
    public void testIsValidEmail_Invalid() {
        assertFalse(SecurBankUtil.isValidEmail(null));
        assertFalse(SecurBankUtil.isValidEmail(""));
        assertFalse(SecurBankUtil.isValidEmail("   "));
        assertFalse(SecurBankUtil.isValidEmail("invalid"));
        assertFalse(SecurBankUtil.isValidEmail("@example.com"));
        assertFalse(SecurBankUtil.isValidEmail("test@"));
        assertFalse(SecurBankUtil.isValidEmail("a@b"));
    }

    @Test
    public void testFormatCurrency_Positive() {
        assertEquals("$100.00", SecurBankUtil.formatCurrency(100.0));
        assertEquals("$1234.56", SecurBankUtil.formatCurrency(1234.56));
        assertEquals("$0.00", SecurBankUtil.formatCurrency(0.0));
    }

    @Test
    public void testFormatCurrency_Negative() {
        assertEquals("-$100.00", SecurBankUtil.formatCurrency(-100.0));
        assertEquals("-$1234.56", SecurBankUtil.formatCurrency(-1234.56));
    }

    @Test
    public void testFormatCurrency_DecimalPrecision() {
        assertEquals("$101.00", SecurBankUtil.formatCurrency(100.999)); // rounds up
        assertEquals("$100.01", SecurBankUtil.formatCurrency(100.009));
    }
}

