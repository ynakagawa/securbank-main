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

package com.adobe.aem.summit.s2024.react.securbank.core.examples;

import com.adobe.aem.summit.s2024.react.securbank.core.services.PdfGenerationService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Example class demonstrating how to use PdfGenerationService to generate PDFs from XDP templates.
 * 
 * This is a reference implementation showing various use cases.
 */
@Component(service = PdfGenerationExample.class)
public class PdfGenerationExample {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerationExample.class);

    @Reference
    private PdfGenerationService pdfGenerationService;

    /**
     * Example 1: Generate a simple account statement PDF.
     */
    public InputStream generateAccountStatement(String customerName, String accountNumber, double balance) 
            throws Exception {
        
        log.info("Generating account statement for customer: {}", customerName);

        // Prepare form data
        Map<String, Object> formData = new HashMap<>();
        formData.put("customerName", customerName);
        formData.put("accountNumber", accountNumber);
        formData.put("balance", String.format("%.2f", balance));
        formData.put("statementDate", java.time.LocalDate.now().toString());
        formData.put("currency", "USD");

        // Generate PDF from XDP template
        String templatePath = "/content/dam/securbank/forms/account-statement.xdp";
        return pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    }

    /**
     * Example 2: Generate a loan application PDF with nested data.
     */
    public InputStream generateLoanApplication(Map<String, Object> applicationData) throws Exception {
        
        log.info("Generating loan application PDF");

        // XDP templates can handle nested structures
        Map<String, Object> formData = new HashMap<>();
        
        // Personal information
        formData.put("applicantFirstName", applicationData.get("firstName"));
        formData.put("applicantLastName", applicationData.get("lastName"));
        formData.put("applicantEmail", applicationData.get("email"));
        formData.put("applicantPhone", applicationData.get("phone"));
        
        // Loan details
        formData.put("loanAmount", applicationData.get("loanAmount"));
        formData.put("loanPurpose", applicationData.get("purpose"));
        formData.put("loanTerm", applicationData.get("term"));
        
        // Financial information
        formData.put("annualIncome", applicationData.get("annualIncome"));
        formData.put("monthlyExpenses", applicationData.get("monthlyExpenses"));
        
        // Application metadata
        formData.put("applicationDate", java.time.LocalDateTime.now().toString());
        formData.put("applicationId", applicationData.getOrDefault("applicationId", "AUTO-" + System.currentTimeMillis()));

        String templatePath = "/content/dam/securbank/forms/loan-application.xdp";
        return pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    }

    /**
     * Example 3: Generate a transaction history PDF with multiple records.
     */
    public InputStream generateTransactionHistory(String accountNumber, java.util.List<Map<String, Object>> transactions) 
            throws Exception {
        
        log.info("Generating transaction history for account: {}", accountNumber);

        Map<String, Object> formData = new HashMap<>();
        formData.put("accountNumber", accountNumber);
        formData.put("reportDate", java.time.LocalDate.now().toString());
        formData.put("totalTransactions", String.valueOf(transactions.size()));

        // For repeating subforms in XDP, you might need to structure data differently
        // This example shows a simple approach - adjust based on your XDP template structure
        for (int i = 0; i < transactions.size() && i < 50; i++) { // Limit to 50 transactions
            Map<String, Object> transaction = transactions.get(i);
            formData.put("transaction" + i + "_date", transaction.get("date"));
            formData.put("transaction" + i + "_description", transaction.get("description"));
            formData.put("transaction" + i + "_amount", transaction.get("amount"));
            formData.put("transaction" + i + "_type", transaction.get("type"));
        }

        String templatePath = "/content/dam/securbank/forms/transaction-history.xdp";
        return pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    }

    /**
     * Example 4: Generate a certificate PDF with dynamic content.
     */
    public InputStream generateCertificate(String recipientName, String certificateType, String issueDate) 
            throws Exception {
        
        log.info("Generating certificate for: {}", recipientName);

        Map<String, Object> formData = new HashMap<>();
        formData.put("recipientName", recipientName);
        formData.put("certificateType", certificateType);
        formData.put("issueDate", issueDate);
        formData.put("certificateNumber", "CERT-" + System.currentTimeMillis());
        formData.put("issuingAuthority", "SecurBank Financial Services");

        String templatePath = "/content/dam/securbank/forms/certificate.xdp";
        return pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    }

    /**
     * Example 5: Generate a report PDF with calculated fields.
     */
    public InputStream generateFinancialReport(Map<String, Object> reportData) throws Exception {
        
        log.info("Generating financial report");

        Map<String, Object> formData = new HashMap<>();
        
        // Copy all report data
        formData.putAll(reportData);
        
        // Add calculated fields
        double totalIncome = getDoubleValue(reportData, "income1") + 
                            getDoubleValue(reportData, "income2") + 
                            getDoubleValue(reportData, "income3");
        double totalExpenses = getDoubleValue(reportData, "expense1") + 
                              getDoubleValue(reportData, "expense2") + 
                              getDoubleValue(reportData, "expense3");
        double netAmount = totalIncome - totalExpenses;
        
        formData.put("totalIncome", String.format("%.2f", totalIncome));
        formData.put("totalExpenses", String.format("%.2f", totalExpenses));
        formData.put("netAmount", String.format("%.2f", netAmount));
        formData.put("reportGeneratedDate", java.time.LocalDateTime.now().toString());

        String templatePath = "/content/dam/securbank/forms/financial-report.xdp";
        return pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    }

    /**
     * Helper method to safely extract double values from map.
     */
    private double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
