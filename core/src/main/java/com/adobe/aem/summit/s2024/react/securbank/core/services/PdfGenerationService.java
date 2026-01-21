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

package com.adobe.aem.summit.s2024.react.securbank.core.services;

import org.osgi.annotation.versioning.ProviderType;

import java.io.InputStream;
import java.util.Map;

/**
 * Service interface for PDF generation using AEM Forms SDK API.
 */
@ProviderType
public interface PdfGenerationService {

    /**
     * Generates a PDF from an XDP template with form data.
     *
     * @param templatePath Path to the XDP template in AEM (e.g., /content/dam/forms/template.xdp)
     * @param formData Map of form field names to values
     * @return InputStream containing the generated PDF
     * @throws Exception if PDF generation fails
     */
    InputStream generatePdfFromXdp(String templatePath, Map<String, Object> formData) throws Exception;

    /**
     * Generates a PDF from an HTML template.
     *
     * @param htmlContent HTML content to convert to PDF
     * @return InputStream containing the generated PDF
     * @throws Exception if PDF generation fails
     */
    InputStream generatePdfFromHtml(String htmlContent) throws Exception;

    /**
     * Renders a PDF form with data.
     *
     * @param formPath Path to the form in AEM
     * @param data XML data to merge with the form
     * @return InputStream containing the rendered PDF
     * @throws Exception if PDF rendering fails
     */
    InputStream renderPdfForm(String formPath, String data) throws Exception;

    /**
     * Converts a document (DOCX, HTML, etc.) to PDF.
     *
     * @param documentInputStream Input stream of the source document
     * @param mimeType MIME type of the source document (e.g., "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
     * @return InputStream containing the converted PDF
     * @throws Exception if conversion fails
     */
    InputStream convertToPdf(InputStream documentInputStream, String mimeType) throws Exception;
}
