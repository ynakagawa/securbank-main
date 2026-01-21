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

import com.adobe.aem.summit.s2024.react.securbank.core.services.PdfGenerationService;
import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.output.api.OutputService;
import com.adobe.fd.output.api.OutputServiceException;
import com.adobe.fd.output.api.PDFOutputOptions;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of PDF generation service using AEM Forms SDK API.
 * 
 * This service demonstrates various ways to create PDFs:
 * 1. Using OutputService to render PDF forms
 * 2. Using FormsService to render adaptive forms
 * 3. Converting documents to PDF
 */
@Component(service = PdfGenerationService.class)
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerationServiceImpl.class);

    @Reference
    private OutputService outputService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public InputStream generatePdfFromXdp(String templatePath, Map<String, Object> formData) throws Exception {
        log.info("Generating PDF from XDP template: {}", templatePath);

        ResourceResolver resolver = null;
        try {
            // Get resource resolver with service user
            Map<String, Object> authInfo = new HashMap<>();
            authInfo.put(ResourceResolverFactory.SUBSERVICE, "pdf-generation");
            resolver = resourceResolverFactory.getServiceResourceResolver(authInfo);

            // Get the XDP template from AEM
            Session session = resolver.adaptTo(Session.class);
            if (session == null) {
                throw new Exception("Unable to get JCR session");
            }

            Node templateNode = session.getNode(templatePath);
            Binary templateBinary = templateNode.getProperty("jcr:data").getBinary();
            InputStream templateStream = templateBinary.getStream();

            // Create Document from template stream
            Document templateDoc = new Document(templateStream);

            // Prepare form data as XML
            String xmlData = buildXmlData(formData);
            Document dataDoc = new Document(new ByteArrayInputStream(xmlData.getBytes("UTF-8")));

            // Create render options
            PDFOutputOptions renderOptions = new PDFOutputOptions();
            renderOptions.setContentRoot(templatePath.substring(0, templatePath.lastIndexOf('/')));

            // Render PDF form
            Document pdfDoc = outputService.generatePDFOutput(templateDoc, dataDoc, renderOptions);

            return pdfDoc.getInputStream();

        } catch (OutputServiceException | RepositoryException | IOException e) {
            log.error("Error generating PDF from XDP template", e);
            throw new Exception("Failed to generate PDF from XDP template", e);
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    @Override
    public InputStream generatePdfFromHtml(String htmlContent) throws Exception {
        log.info("Generating PDF from HTML content");

        try {
            // Convert HTML to PDF using OutputService
            // Note: This requires HTML2PDF service to be configured
            Document htmlDoc = new Document(new ByteArrayInputStream(htmlContent.getBytes("UTF-8")));
            
            // Create empty data document for HTML conversion
            Document dataDoc = new Document(new ByteArrayInputStream("".getBytes()));

            PDFOutputOptions pdfOptions = new PDFOutputOptions();
            pdfOptions.setContentRoot("");

            // Generate PDF from HTML
            Document pdfDoc = outputService.generatePDFOutput(htmlDoc, dataDoc, pdfOptions);

            return pdfDoc.getInputStream();

        } catch (OutputServiceException | IOException e) {
            log.error("Error generating PDF from HTML", e);
            throw new Exception("Failed to generate PDF from HTML", e);
        }
    }

    @Override
    public InputStream renderPdfForm(String formPath, String data) throws Exception {
        log.info("Rendering PDF form: {}", formPath);

        ResourceResolver resolver = null;
        try {
            // Get resource resolver with service user
            Map<String, Object> authInfo = new HashMap<>();
            authInfo.put(ResourceResolverFactory.SUBSERVICE, "pdf-generation");
            resolver = resourceResolverFactory.getServiceResourceResolver(authInfo);

            // Get the form from AEM
            Session session = resolver.adaptTo(Session.class);
            if (session == null) {
                throw new Exception("Unable to get JCR session");
            }

            Node formNode = session.getNode(formPath);
            Binary formBinary = formNode.getProperty("jcr:data").getBinary();
            InputStream formStream = formBinary.getStream();

            // Create Document from form stream
            Document formDoc = new Document(formStream);
            
            // Create Document from data
            Document dataDoc = new Document(new ByteArrayInputStream(data.getBytes("UTF-8")));

            // Create render options
            PDFOutputOptions renderOptions = new PDFOutputOptions();
            renderOptions.setContentRoot(formPath.substring(0, formPath.lastIndexOf('/')));

            // Render PDF form with data
            Document pdfDoc = outputService.generatePDFOutput(formDoc, dataDoc, renderOptions);

            return pdfDoc.getInputStream();

        } catch (OutputServiceException | RepositoryException | IOException e) {
            log.error("Error rendering PDF form", e);
            throw new Exception("Failed to render PDF form", e);
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    @Override
    public InputStream convertToPdf(InputStream documentInputStream, String mimeType) throws Exception {
        log.info("Converting document to PDF, MIME type: {}", mimeType);

        try {
            // Use OutputService to convert document to PDF
            Document sourceDoc = new Document(documentInputStream);
            
            // Create empty data document for document conversion
            Document dataDoc = new Document(new ByteArrayInputStream("".getBytes()));

            PDFOutputOptions pdfOptions = new PDFOutputOptions();
            // Note: MIME type handling may vary based on AEM Forms version

            Document pdfDoc = outputService.generatePDFOutput(sourceDoc, dataDoc, pdfOptions);

            return pdfDoc.getInputStream();

        } catch (OutputServiceException | IOException e) {
            log.error("Error converting document to PDF", e);
            throw new Exception("Failed to convert document to PDF", e);
        }
    }

    /**
     * Builds XML data string from form data map.
     *
     * @param formData Map of field names to values
     * @return XML data string
     */
    private String buildXmlData(Map<String, Object> formData) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><form>");
        
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            xml.append("<").append(entry.getKey()).append(">");
            xml.append(escapeXml(entry.getValue().toString()));
            xml.append("</").append(entry.getKey()).append(">");
        }
        
        xml.append("</form>");
        return xml.toString();
    }

    /**
     * Escapes XML special characters.
     */
    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
