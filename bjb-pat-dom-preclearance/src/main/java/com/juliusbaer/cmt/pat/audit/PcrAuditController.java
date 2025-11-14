package com.juliusbaer.cmt.pat.audit;

import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pcr-audit") // Base URL for this controller
public class PcrAuditController {
    private static final Logger SVL = Loggers.SVL;

    private final PcrAuditService pcrAuditService;

    // Inject your existing service
    public PcrAuditController(PcrAuditService pcrAuditService) {
        this.pcrAuditService = pcrAuditService;
    }

    @GetMapping(path = "/{caseId}/download", produces ="application/pdf") // Using your path from the first example
    public ResponseEntity<byte[]> downloadAuditPdf(@PathVariable("caseId") String caseId, @RequestParam(value = "caseSequenceValue") String caseSequenceValue) {

        byte[] pdfBytes = pcrAuditService.getAuditForDownload(caseId, caseSequenceValue);
        String fileName = "audit-report-" + caseId + ".pdf";

        SVL.info("endpoint=/download=GET,action={},status={}",
                LogVocab.ActionSVL.RESPONSE_SENT.name(),
                LogVocab.StatusSVL.SUCCESS.name());

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(pdfBytes);
    }
}
