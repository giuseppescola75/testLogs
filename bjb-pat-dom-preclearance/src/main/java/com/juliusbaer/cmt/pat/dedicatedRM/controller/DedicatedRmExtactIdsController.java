package com.juliusbaer.cmt.pat.dedicatedRM.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.juliusbaer.cmt.pat.dedicatedRM.service.DedicatedRmExtactIdsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exb-members")
@RequiredArgsConstructor
public class DedicatedRmExtactIdsController {


    private final DedicatedRmExtactIdsService dedicatedRmExtactIdsService;

    @GetMapping("/{userId}/simplify")
    public ResponseEntity<ObjectNode> getSimpleRmInfo(@PathVariable("userId") String userId) {
        ObjectNode response = dedicatedRmExtactIdsService.getSimpleRmInfo(userId);
        return ResponseEntity.ok(response);
    }
}
