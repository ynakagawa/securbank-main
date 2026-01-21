# XDP Template PDF Generation - Complete Example

This document provides a complete example of generating PDFs from XDP templates using the AEM Forms SDK API.

## Overview

The `PdfGenerationService` provides a method `generatePdfFromXdp()` that takes:
- **Template Path**: Path to the XDP template in AEM DAM (e.g., `/content/dam/forms/template.xdp`)
- **Form Data**: Map of field names to values that will populate the PDF

## Sample Code

### Example 1: Simple Servlet Usage

```java
@Component(service = SlingAllMethodsServlet.class, property = {
    "sling.servlet.methods=GET,POST",
    "sling.servlet.paths=/bin/securbank/generate-pdf"
})
public class GeneratePdfServlet extends SlingAllMethodsServlet {
    
    @Reference
    private PdfGenerationService pdfGenerationService;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get template path
        String templatePath = request.getParameter("template");
        
        // Prepare form data
        Map<String, Object> formData = new HashMap<>();
        formData.put("customerName", request.getParameter("customerName"));
        formData.put("accountNumber", request.getParameter("accountNumber"));
        formData.put("balance", request.getParameter("balance"));
        
        // Generate PDF
        InputStream pdfStream = pdfGenerationService.generatePdfFromXdp(templatePath, formData);
        
        // Stream to response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=statement.pdf");
        IOUtils.copy(pdfStream, response.getOutputStream());
    }
}
```

### Example 2: Using in a Sling Model

```java
@Model(adaptables = SlingHttpServletRequest.class)
public class AccountStatementModel {
    
    @Reference
    private PdfGenerationService pdfGenerationService;
    
    @Inject
    private SlingHttpServletRequest request;
    
    public InputStream generateStatement() throws Exception {
        Map<String, Object> formData = new HashMap<>();
        formData.put("customerName", "John Doe");
        formData.put("accountNumber", "1234567890");
        formData.put("balance", "5000.00");
        formData.put("statementDate", LocalDate.now().toString());
        
        return pdfGenerationService.generatePdfFromXdp(
            "/content/dam/securbank/forms/account-statement.xdp", 
            formData
        );
    }
}
```

### Example 3: Complete Service Method

```java
@Service
public class AccountService {
    
    @Reference
    private PdfGenerationService pdfGenerationService;
    
    public InputStream generateAccountStatement(Account account) throws Exception {
        // Prepare form data from account object
        Map<String, Object> formData = new HashMap<>();
        formData.put("customerName", account.getCustomerName());
        formData.put("accountNumber", account.getAccountNumber());
        formData.put("accountType", account.getAccountType());
        formData.put("balance", String.format("%.2f", account.getBalance()));
        formData.put("currency", account.getCurrency());
        formData.put("statementDate", LocalDate.now().toString());
        formData.put("statementPeriod", account.getStatementPeriod());
        
        // Address information
        formData.put("addressLine1", account.getAddress().getLine1());
        formData.put("addressLine2", account.getAddress().getLine2());
        formData.put("city", account.getAddress().getCity());
        formData.put("state", account.getAddress().getState());
        formData.put("zipCode", account.getAddress().getZipCode());
        
        // Generate PDF
        String templatePath = "/content/dam/securbank/forms/account-statement.xdp";
        return pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    }
}
```

## XDP Template Structure

Your XDP template should have form fields that match the keys in your `formData` map. For example:

### XDP Form Fields:
- `customerName` (text field)
- `accountNumber` (text field)
- `balance` (numeric field)
- `statementDate` (date field)
- `addressLine1` (text field)
- etc.

### Form Data Map:
```java
Map<String, Object> formData = new HashMap<>();
formData.put("customerName", "John Doe");  // Matches XDP field name
formData.put("accountNumber", "1234567890");
formData.put("balance", "5000.00");
```

## XML Data Format

The service converts your Map to XML format internally. The XML structure looks like:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<form>
    <customerName>John Doe</customerName>
    <accountNumber>1234567890</accountNumber>
    <balance>5000.00</balance>
    <statementDate>2024-01-13</statementDate>
</form>
```

## Handling Nested Data

For nested structures in XDP templates (subforms), structure your data accordingly:

```java
// For a subform named "personalInfo"
formData.put("personalInfo.firstName", "John");
formData.put("personalInfo.lastName", "Doe");
formData.put("personalInfo.email", "john.doe@example.com");

// Or use dot notation for nested fields
formData.put("applicant.personal.firstName", "John");
formData.put("applicant.personal.lastName", "Doe");
formData.put("applicant.financial.annualIncome", "75000");
```

## Handling Repeating Subforms

For repeating data (like transaction lists), you may need to structure data with indices:

```java
// For a repeating subform "transactions"
for (int i = 0; i < transactions.size(); i++) {
    Transaction txn = transactions.get(i);
    formData.put("transactions[" + i + "].date", txn.getDate());
    formData.put("transactions[" + i + "].amount", txn.getAmount());
    formData.put("transactions[" + i + "].description", txn.getDescription());
}
```

## Error Handling

Always wrap PDF generation in try-catch blocks:

```java
try {
    InputStream pdfStream = pdfGenerationService.generatePdfFromXdp(templatePath, formData);
    // Process PDF...
} catch (Exception e) {
    log.error("Error generating PDF", e);
    // Handle error appropriately
    throw new RuntimeException("Failed to generate PDF", e);
}
```

## Common Issues and Solutions

### Issue 1: Template Not Found
**Error**: `Unable to get JCR session` or `Node not found`
**Solution**: 
- Verify template path exists in AEM DAM
- Ensure service user has read access to `/content/dam`
- Check that template is uploaded as `.xdp` file

### Issue 2: Fields Not Populating
**Error**: PDF generates but fields are empty
**Solution**:
- Verify field names in formData match XDP template field names exactly (case-sensitive)
- Check XDP template field bindings
- Ensure data types match (text fields get strings, numeric fields get numbers)

### Issue 3: Special Characters Not Displaying
**Error**: Special characters appear as boxes or question marks
**Solution**:
- Ensure XML encoding is UTF-8
- Escape XML special characters (`&`, `<`, `>`, `"`, `'`)
- The service handles escaping automatically, but verify your data

### Issue 4: Memory Issues with Large PDFs
**Error**: OutOfMemoryError
**Solution**:
- Stream PDF directly to response instead of loading into memory
- Process large datasets in chunks
- Consider using OutputService directly for more control

## Testing

### Test via Browser:
```
GET http://localhost:4502/bin/securbank/generate-pdf?template=/content/dam/forms/account-statement.xdp&customerName=John%20Doe&accountNumber=1234567890&balance=5000.00
```

### Test via cURL:
```bash
curl -u admin:admin \
  "http://localhost:4502/bin/securbank/generate-pdf?template=/content/dam/forms/account-statement.xdp&customerName=John%20Doe&accountNumber=1234567890&balance=5000.00" \
  --output statement.pdf
```

### Test via Postman:
1. Method: GET
2. URL: `http://localhost:4502/bin/securbank/generate-pdf`
3. Params:
   - `template`: `/content/dam/forms/account-statement.xdp`
   - `customerName`: `John Doe`
   - `accountNumber`: `1234567890`
   - `balance`: `5000.00`

## Best Practices

1. **Template Management**: Store all XDP templates in a dedicated DAM folder (e.g., `/content/dam/securbank/forms/`)

2. **Field Naming**: Use consistent naming conventions for XDP fields (camelCase or snake_case)

3. **Data Validation**: Validate form data before generating PDF to avoid runtime errors

4. **Error Handling**: Always handle exceptions and provide meaningful error messages

5. **Resource Management**: Close InputStreams properly to avoid memory leaks

6. **Caching**: Consider caching generated PDFs for frequently accessed documents

7. **Security**: Ensure proper authentication and authorization for PDF generation endpoints

8. **Logging**: Log PDF generation activities for audit and debugging purposes

## Additional Resources

- [AEM Forms Documentation](https://experienceleague.adobe.com/docs/experience-manager-forms-cloud-service/forms/using-forms/aem-forms-cloud-service.html)
- [XDP Template Design Guide](https://helpx.adobe.com/livecycle/help/designing-forms-layouts.html)
- [Output Service API](https://helpx.adobe.com/experience-manager/6-5/forms/javadocs/com/adobe/fd/output/api/OutputService.html)
