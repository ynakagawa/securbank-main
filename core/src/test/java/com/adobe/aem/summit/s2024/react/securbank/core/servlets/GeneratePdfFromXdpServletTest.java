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

package com.adobe.aem.summit.s2024.react.securbank.core.servlets;

import com.adobe.aem.summit.s2024.react.securbank.core.services.PdfGenerationService;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GeneratePdfFromXdpServletTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public AemContext context = new AemContext();

    @Mock
    private PdfGenerationService pdfGenerationService;

    private GeneratePdfFromXdpServlet servlet;

    @Before
    public void setUp() {
        servlet = new GeneratePdfFromXdpServlet();
        context.registerService(PdfGenerationService.class, pdfGenerationService);
        context.registerInjectActivateService(servlet);
    }

    @Test
    public void testDoGet_MissingTemplate_Returns400() throws Exception {
        servlet.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
        assertTrue(context.response().getOutputAsString().contains("Template path parameter is required"));
    }

    @Test
    public void testDoPost_MissingTemplate_Returns400() throws Exception {
        servlet.doPost(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
    }

    @Test
    public void testDoGet_WithTemplate_ReturnsPdf() throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("template", new String[]{"/content/dam/forms/test.xdp"});
        paramMap.put("customerName", new String[]{"John"});
        context.request().setParameterMap(paramMap);

        byte[] pdfBytes = new byte[]{'P', 'D', 'F'};
        InputStream pdfStream = new ByteArrayInputStream(pdfBytes);
        when(pdfGenerationService.generatePdfFromXdp(anyString(), anyMap())).thenReturn(pdfStream);

        servlet.doGet(context.request(), context.response());

        assertEquals(200, context.response().getStatus());
        assertEquals("application/pdf", context.response().getContentType());
        assertTrue(context.response().getOutputAsString().contains("PDF"));
    }

    @Test
    public void testDoGet_ServiceThrows_Returns500() throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("template", new String[]{"/content/dam/forms/test.xdp"});
        context.request().setParameterMap(paramMap);

        when(pdfGenerationService.generatePdfFromXdp(anyString(), anyMap()))
                .thenThrow(new RuntimeException("Service error"));

        servlet.doGet(context.request(), context.response());

        assertEquals(500, context.response().getStatus());
        assertTrue(context.response().getOutputAsString().contains("error"));
    }
}
