package com.juliusbaer.cmt.pat.localCompliance;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/localCompliance")
@RequiredArgsConstructor
public class LocalComplianceController {
    private final LocalComplianceService localComplianceService;

    @GetMapping("/getVariableFromDecisionTable")
    public String getVariableFromDecisionTable(@RequestParam(name = "decisionKey", required = true) String decisionKey, @RequestParam(name = "inputVariableName", required = true) String inputVariableName, @RequestParam(name = "inputVariableValue", required = true) String inputVariableValue, @RequestParam(name = "outputVariableName", required = true) String outputVariableName) {

        return localComplianceService.getVariableFromDecisionTable(decisionKey, inputVariableName, inputVariableValue, outputVariableName);

    }
}
