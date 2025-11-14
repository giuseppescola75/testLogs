package com.juliusbaer.cmt.pat.common.services.device;

import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device/")
public class DeviceController {
    private static final Logger SVL = Loggers.SVL;
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    //TODO: add the caseId
    @GetMapping("/is-mobile")
    public ResponseEntity<Boolean> checkDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        boolean isMobile = deviceService.isMobileDevice(userAgent);
        SVL.info("endpoint=/is-mobile=GET,action={},status={},isMobile={}",
                LogVocab.ActionSVL.RESPONSE_SENT.name(),
                LogVocab.StatusSVL.SUCCESS.name(),
                isMobile);
        return ResponseEntity.ok(isMobile);
    }
}
