# AEM Forms SDK API - PDF Generation Guide

This guide explains how to use the AEM Forms SDK API to create PDF files in your AEM application.

## Overview

The AEM Forms SDK API provides several services for PDF generation:
- **OutputService**: Renders PDF forms from XDP templates and converts documents to PDF
- **FormsService**: Renders adaptive forms to PDF
- **Document Services**: Various utilities for PDF manipulation

## Key Classes

### Document (`com.adobe.aemfd.docmanager.Document`)
- Wraps PDF and other document types
- Used to pass documents between AEM Forms services
- Provides `getInputStream()` to read document content

### OutputService (`com.adobe.fd.output.api.OutputService`)
- Main service for PDF generation
- Methods:
  - `generatePDFOutput(Document template, Document data, PDFOutputOptions options)`: Generates PDF from template and data
  - `renderPDFForm(...)`: Renders PDF forms (varies by AEM version)

### PDFOutputOptions (`com.adobe.fd.output.api.PDFOutputOptions`)
- Configuration options for PDF generation
- Properties:
  - `setContentRoot(String)`: Sets the content root path
  - `setAcrobatVersion(...)`: Sets Acrobat version compatibility

## Usage Examples

### 1. Generate PDF from XDP Template

```java
@Reference
private OutputService outputService;

@Reference
private ResourceResolverFactory resourceResolverFactory;

public InputStream generatePdfFromXdp(String templatePath, Map<String, Object> formData) throws Exception {
    ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(
        Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "pdf-generation")
    );
    
    try {
        Session session = resolver.adaptTo(Session.class);
        Node templateNode = session.getNode(templatePath);
        Binary templateBinary = templateNode.getProperty("jcr:data").getBinary();
        
        // Create Document from template
        Document templateDoc = new Document(templateBinary.getStream());
        
        // Prepare form data as XML
        String xmlData = buildXmlData(formData);
        Document dataDoc = new Document(new ByteArrayInputStream(xmlData.getBytes("UTF-8")));
        
        // Configure options
        PDFOutputOptions options = new PDFOutputOptions();
        options.setContentRoot(templatePath.substring(0, templatePath.lastIndexOf('/')));
        
        // Generate PDF
        Document pdfDoc = outputService.generatePDFOutput(templateDoc, dataDoc, options);
        
        return pdfDoc.getInputStream();
    } finally {
        resolver.close();
    }
}
```

### 2. Generate PDF from HTML

```java
public InputStream generatePdfFromHtml(String htmlContent) throws Exception {
    // Create Document from HTML
    Document htmlDoc = new Document(new ByteArrayInputStream(htmlContent.getBytes("UTF-8")));
    Document dataDoc = new Document(new ByteArrayInputStream("".getBytes()));
    
    PDFOutputOptions options = new PDFOutputOptions();
    options.setContentRoot("");
    
    // Generate PDF
    Document pdfDoc = outputService.generatePDFOutput(htmlDoc, dataDoc, options);
    
    return pdfDoc.getInputStream();
}
```

### 3. Convert Document to PDF (DOCX, etc.)

```java
public InputStream convertToPdf(InputStream documentInputStream, String mimeType) throws Exception {
    Document sourceDoc = new Document(documentInputStream);
    Document dataDoc = new Document(new ByteArrayInputStream("".getBytes()));
    
    PDFOutputOptions options = new PDFOutputOptions();
    
    Document pdfDoc = outputService.generatePDFOutput(sourceDoc, dataDoc, options);
    
    return pdfDoc.getInputStream();
}
```

### 4. Using in a Servlet/Sling Model

```java
@Component(service = Servlet.class, property = {
    "sling.servlet.methods=GET",
    "sling.servlet.paths=/bin/generate-pdf"
})
public class PdfGenerationServlet extends SlingAllMethodsServlet {
    
    @Reference
    private PdfGenerationService pdfGenerationService;
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String templatePath = request.getParameter("template");
            Map<String, Object> formData = extractFormData(request);
            
            InputStream pdfStream = pdfGenerationService.generatePdfFromXdp(templatePath, formData);
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=generated.pdf");
            
            IOUtils.copy(pdfStream, response.getOutputStream());
            
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
```

## XML Data Format

When generating PDFs from XDP templates, the data should be in XML format:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<form>
    <field1>Value 1</field1>
    <field2>Value 2</field2>
    <subform>
        <nestedField>Nested Value</nestedField>
    </subform>
</form>
```

## Service User Configuration

For production use, configure a service user for PDF generation:

1. Create a service user mapping in `ui.config`:
   ```json
   {
     "user": "pdf-generation-service",
     "paths": ["/content/dam"]
   }
   ```

2. Use it in your code:
   ```java
   Map<String, Object> authInfo = new HashMap<>();
   authInfo.put(ResourceResolverFactory.SUBSERVICE, "pdf-generation");
   ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(authInfo);
   ```

## Dependencies

The following dependencies are required (already added to `pom.xml`):

```xml
<dependency>
    <groupId>com.adobe.aem</groupId>
    <artifactId>aem-sdk-api</artifactId>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.adobe.aem</groupId>
    <artifactId>aem-forms-sdk-api</artifactId>
    <scope>provided</scope>
</dependency>
```

## Common Issues

1. **Missing Service User**: Ensure service user is configured for accessing DAM assets
2. **Template Not Found**: Verify template path exists in AEM DAM
3. **Invalid XML Data**: Ensure XML data matches template field structure
4. **Memory Issues**: For large PDFs, consider streaming or chunked processing

## Additional Resources

- [AEM Forms Documentation](https://experienceleague.adobe.com/docs/experience-manager-forms-cloud-service/forms/using-forms/aem-forms-cloud-service.html)
- [Output Service API Reference](https://helpx.adobe.com/experience-manager/6-5/forms/javadocs/com/adobe/fd/output/api/OutputService.html)
- [Document Manager API](https://helpx.adobe.com/experience-manager/6-5/forms/javadocs/com/adobe/aemfd/docmanager/Document.html)
