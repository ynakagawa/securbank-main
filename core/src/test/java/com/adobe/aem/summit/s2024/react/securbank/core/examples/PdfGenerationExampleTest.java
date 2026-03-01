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
 */

package com.adobe.aem.summit.s2024.react.securbank.core.examples;

import com.adobe.aem.summit.s2024.react.securbank.core.services.PdfGenerationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PdfGenerationExampleTest {

    @Mock
    private PdfGenerationService pdfGenerationService;

    private PdfGenerationExample example;

    @Before
    public void setUp() throws Exception {
        example = new PdfGenerationExample();
        java.lang.reflect.Field field = PdfGenerationExample.class.getDeclaredField("pdfGenerationService");
        field.setAccessible(true);
        field.set(example, pdfGenerationService);
    }

    @Test
    public void testGenerateAccountStatement() throws Exception {
        InputStream mockStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        when(pdfGenerationService.generatePdfFromXdp(anyString(), anyMap())).thenReturn(mockStream);

        InputStream result = example.generateAccountStatement("Jane Doe", "1234567890", 5000.50);

        assertNotNull(result);
    }

    @Test
    public void testGenerateLoanApplication() throws Exception {
        InputStream mockStream = new ByteArrayInputStream(new byte[]{1});
        when(pdfGenerationService.generatePdfFromXdp(anyString(), anyMap())).thenReturn(mockStream);

        Map<String, Object> data = new HashMap<>();
        data.put("firstName", "John");
        data.put("lastName", "Doe");
        data.put("email", "john@example.com");
        data.put("loanAmount", "100000");
        data.put("purpose", "Home");
        data.put("term", "30");
        data.put("annualIncome", "75000");
        data.put("monthlyExpenses", "3000");

        InputStream result = example.generateLoanApplication(data);

        assertNotNull(result);
    }

    @Test
    public void testGenerateFinancialReport() throws Exception {
        InputStream mockStream = new ByteArrayInputStream(new byte[]{1});
        when(pdfGenerationService.generatePdfFromXdp(anyString(), anyMap())).thenReturn(mockStream);

        Map<String, Object> data = new HashMap<>();
        data.put("revenue", 100000.0);
        data.put("expenses", 60000.0);
        data.put("taxRate", 0.25);

        InputStream result = example.generateFinancialReport(data);

        assertNotNull(result);
    }

    @Test
    public void testGenerateFinancialReport_WithStringNumbers() throws Exception {
        InputStream mockStream = new ByteArrayInputStream(new byte[]{1});
        when(pdfGenerationService.generatePdfFromXdp(anyString(), anyMap())).thenReturn(mockStream);

        Map<String, Object> data = new HashMap<>();
        data.put("revenue", "100000");
        data.put("expenses", "60000");

        InputStream result = example.generateFinancialReport(data);

        assertNotNull(result);
    }
}
