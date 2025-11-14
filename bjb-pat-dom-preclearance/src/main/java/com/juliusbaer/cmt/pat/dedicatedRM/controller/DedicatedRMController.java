package com.juliusbaer.cmt.pat.dedicatedRM.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.juliusbaer.cmt.pat.dedicatedRM.service.DedicatedRMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exb-members")
@RequiredArgsConstructor
public class DedicatedRMController {

    private final DedicatedRMService dedicatedRMService;

    // GET /api/exb-members/{userId}/dedicatedrm?dedicatedRm=true|false|1|0|(missing)
    @GetMapping("/{userId}/dedicatedrm")
    public ObjectNode getDedicatedRm(
            @PathVariable("userId") String userId,
            @RequestParam(name = "dedicatedRm", required = false) String dedicatedParam
    ) {
        Boolean dedicated = normalizeDedicated(dedicatedParam); // null = both
        return dedicatedRMService.fetchByDedicated(userId, dedicated);
    }

    private static Boolean normalizeDedicated(String v) {
        if (v == null || v.isBlank()) return null; // both
        String s = v.trim().toLowerCase();
        if (s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("y")) return Boolean.TRUE;
        if (s.equals("false") || s.equals("0") || s.equals("no")  || s.equals("n")) return Boolean.FALSE;
        return null;
    }
}
