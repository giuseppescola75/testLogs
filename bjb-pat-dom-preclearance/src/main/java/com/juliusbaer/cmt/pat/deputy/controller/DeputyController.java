package com.juliusbaer.cmt.pat.deputy.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.juliusbaer.cmt.pat.deputy.service.DeputyService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deputies")
@RequiredArgsConstructor
public class DeputyController {
    private final DeputyService deputyService;


    @GetMapping("/deputyDropdown/{userId}")
    public ArrayNode getDeputiesFilteredForDropdown(@PathVariable(name = "userId") String userId) {
        return deputyService.getDeputiesFilteredForDropdown(userId);
    }

}
