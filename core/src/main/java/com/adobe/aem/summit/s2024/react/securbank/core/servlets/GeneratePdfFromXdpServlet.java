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

package com.adobe.aem.summit.s2024.react.securbank.core.servlets;

import com.adobe.aem.summit.s2024.react.securbank.core.services.PdfGenerationService;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample servlet demonstrating how to generate a PDF from an XDP template.
 * 
 * Usage:
 * GET /bin/securbank/generate-pdf?template=/content/dam/forms/account-statement.xdp&customerName=John%20Doe&accountNumber=1234567890
 * 
 * POST /bin/securbank/generate-pdf
 * Body (form data or JSON):
 * {
 *   "template": "/content/dam/forms/account-statement.xdp",
 *   "customerName": "John Doe",
 *   "accountNumber": "1234567890",
 *   "balance": "5000.00"
 * }
 */
@Component(service = SlingAllMethodsServlet.class, property = {
    "sling.servlet.methods=" + org.apache.sling.api.servlets.HttpConstants.METHOD_GET + "," + org.apache.sling.api.servlets.HttpConstants.METHOD_POST,
    "sling.servlet.paths=/bin/securbank/generate-pdf",
    "sling.auth.requirements=-/bin/securbank/generate-pdf"
})
public class GeneratePdfFromXdpServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(GeneratePdfFromXdpServlet.class);

    private static final String PARAM_TEMPLATE = "template";
    private static final String PARAM_FILENAME = "filename";
    private static final String DEFAULT_FILENAME = "generated-document.pdf";

    @Reference
    private PdfGenerationService pdfGenerationService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get template path from request parameter
            String templatePath = request.getParameter(PARAM_TEMPLATE);
            if (templatePath == null || templatePath.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Template path parameter is required\"}");
                return;
            }

            // Extract form data from request parameters
            Map<String, Object> formData = extractFormData(request);

            log.info("Generating PDF from XDP template: {}, with {} fields", templatePath, formData.size());

            // Generate PDF using the service
            InputStream pdfStream = pdfGenerationService.generatePdfFromXdp(templatePath, formData);

            // Set response headers
            String filename = request.getParameter(PARAM_FILENAME);
            if (filename == null || filename.trim().isEmpty()) {
                filename = DEFAULT_FILENAME;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            // Stream PDF to response
            IOUtils.copy(pdfStream, response.getOutputStream());
            response.getOutputStream().flush();

            log.info("PDF generated successfully: {}", filename);

        } catch (Exception e) {
            log.error("Error generating PDF from XDP template", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Extracts form data from request parameters.
     * Excludes system parameters like 'template' and 'filename'.
     *
     * @param request The Sling HTTP request
     * @return Map of form field names to values
     */
    private Map<String, Object> extractFormData(SlingHttpServletRequest request) {
        Map<String, Object> formData = new HashMap<>();

        // Get all request parameters
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            
            // Skip system parameters
            if (PARAM_TEMPLATE.equals(key) || PARAM_FILENAME.equals(key)) {
                continue;
            }

            // Get the first value (for simple fields)
            // For multi-value fields, you might want to handle differently
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                if (values.length == 1) {
                    formData.put(key, values[0]);
                } else {
                    // Handle multi-value fields as array
                    formData.put(key, values);
                }
            }
        }

        return formData;
    }
}
