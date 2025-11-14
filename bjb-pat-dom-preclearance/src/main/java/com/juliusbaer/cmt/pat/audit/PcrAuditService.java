package com.juliusbaer.cmt.pat.audit;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.flowable.audit.api.AuditService;
import com.flowable.audit.api.runtime.AuditInstance;

// Import the iText 7/8/9 libraries
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

@Service
public class PcrAuditService {
    private static final Logger ATL = Loggers.ATL;
    private static final Logger EXL = Loggers.EXL;

    public static final String BJB_GLOBAL = "bjb-global";
    private final AuditService auditService;

    public PcrAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

    public byte[] getAuditForDownload(String caseId, String caseSequenceValue) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Use try-with-resources to ensure the document is closed
        try (Document document = new Document(pdfDoc)) {

            // Add a title
            Paragraph title = new Paragraph("PCR Audit Report: case " + caseSequenceValue)
                    .setUnderline()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); // Add some space

            // 4. Loop over your data and build the PDF
            for (Map<String, Object> item : this.getAuditEntries(caseId)) {
                String creationTime = (String) item.get("creationTime");
                String creator = (String) item.get("creator");
                String message = (String) item.get("message");

                document.add(new Paragraph("Creation time: " + creationTime));
                document.add(new Paragraph("User: " + creator));
                document.add(new Paragraph("Message: " + message));
                document.add(new Paragraph("\n")); // Add a blank line
            }

        } catch (Exception e) {
            // Handle exceptions in your real code
            EXL.error("action={},status={}", LogVocab.ActionEXL.THROWN.name(), LogVocab.StatusEXL.ERROR.name());
            throw new RuntimeException("Error generating PDF audit", e);
        }
        ATL.info("action={},status={},caseId={}", LogVocab.ActionATL.COMPLETED.name(), LogVocab.StatusATL.SUCCESS.name(),caseId);
        return baos.toByteArray();
    }


    private List<Map<String, Object>> getAuditEntries(String caseId) {
        List<AuditInstance> entries = getLogEntries(caseId);
        List<Map<String, Object>> wrappedEntries = new ArrayList<>();
        if (entries != null) {
            for (AuditInstance auditEntry : entries) {
                wrappedEntries.add(getMapFromAuditEntry(auditEntry));
            }
        }
        return wrappedEntries;
    }

    private List<AuditInstance> getLogEntries(String scopeId) {
        return auditService.createAuditInstanceQuery().scopeId(scopeId).list();
    }

    protected Map<String, Object> getMapFromAuditEntry(AuditInstance audit) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("dd.MM.yyyy HH:mm:ss");
        String message = audit.getPayload().get("message").toString();
        String creatorId = audit.getCreatorId();
        Date creationDate = audit.getCreationTime();
        String formattedDate = sdf.format(creationDate);
        String fullMessage = "[" + formattedDate + "] " + creatorId + ": " + message;
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("message", message);
        wrapper.put("creator", creatorId);
        wrapper.put("creationTime", formattedDate);
        wrapper.put("fullMessage", fullMessage);
        return wrapper;
    }
}
