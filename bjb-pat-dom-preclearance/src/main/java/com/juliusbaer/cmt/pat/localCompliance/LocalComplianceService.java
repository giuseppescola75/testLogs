package com.juliusbaer.cmt.pat.localCompliance;

import org.flowable.dmn.api.DmnDecisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LocalComplianceService {
    private static final String TENANT_ID = "bjb-global";
    private final static Logger LOGGER = LoggerFactory.getLogger(LocalComplianceService.class);
    private final DmnDecisionService dmnDecisionService;

    public LocalComplianceService(DmnDecisionService dmnDecisionService) {
        this.dmnDecisionService = dmnDecisionService;
    }

    public String getVariableFromDecisionTable(String decisionKey, String inputVariableName, String inputVariableValue, String outputVariableName) {
        Map<String, Object> result = dmnDecisionService.createExecuteDecisionBuilder().tenantId(TENANT_ID).decisionKey(decisionKey).variable(inputVariableName, inputVariableValue).executeDecisionWithSingleResult();

        if (result == null) {
            LOGGER.warn("No decision result found for decisionKey='{}', input='{}'='{}'", decisionKey, inputVariableName, inputVariableValue);
            return "";
        }

        Object outputValue = result.get(outputVariableName);
        if (outputValue == null) {
            LOGGER.warn("Output variable '{}' not found in decision result for decisionKey='{}'", outputVariableName, decisionKey);
            return "";
        }

        return outputValue.toString();
    }

}
