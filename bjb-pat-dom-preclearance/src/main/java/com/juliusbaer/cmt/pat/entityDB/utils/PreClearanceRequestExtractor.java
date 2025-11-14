package com.juliusbaer.cmt.pat.entityDB.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PreClearanceRequestExtractor {

    private final RuntimeService runtimeService;
    private final CmmnRuntimeService cmmnRuntimeService;
    private final CmmnHistoryService cmmnHistoryService;
    private static final Logger LOG = LoggerFactory.getLogger(PreClearanceRequestExtractor.class);


    public PreClearanceRequestData extract(DelegateExecution execution) {
        String caseId = runtimeService.getEntityLinkParentsForProcessInstance(execution.getRootProcessInstanceId()).stream()
                .filter(link -> "root".equals(link.getHierarchyType()))
                .findFirst()
                .orElseThrow()
                .getScopeId();


        Date createdOn = cmmnHistoryService.createHistoricCaseInstanceQuery()
                .caseInstanceId(caseId)
                .singleResult()
                .getStartTime();

        String status = cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(caseId)
                .singleResult()
                .getBusinessStatus();

        LOG.info("Received Status: {}", status);


        String requester = getStringValue(caseId, "ownerName");

        String requestType = getStringValue(caseId, "requestType");

        String employeeType = getStringValue(caseId, "employeeType");

        String financialInstrumentType = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/financialInstrumentType");

        String fiName = "";
        String valor = "";
        String isin = "";

        switch (Objects.requireNonNull(financialInstrumentType).toLowerCase()) {
            case "shares" -> {
                fiName = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/financialInstrument/bjbNameLongProduct");
                valor = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/financialInstrument/valorNo");
                isin = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/financialInstrument/isin");
            }
            case "underlying" -> {
                String distribution = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/distribution");
                if (distribution != null && distribution.equals("different")) {
                    fiName = getJsonArrayValues(
                            caseId,
                            "preClearanceRequest",
                            "/financialInstruments/financialInstrumentsUnderlyingDistributionDifferent",
                            "/financialInstrument/bjbNameLongProduct"
                    );
                    valor = getJsonArrayValues(
                            caseId,
                            "preClearanceRequest",
                            "/financialInstruments/financialInstrumentsUnderlyingDistributionDifferent",
                            "/financialInstrument/valorNo"
                    );
                    isin = getJsonArrayValues(
                            caseId,
                            "preClearanceRequest",
                            "/financialInstruments/financialInstrumentsUnderlyingDistributionDifferent",
                            "/financialInstrument/isin"
                    );
                } else if (distribution != null && distribution.equals("more")) {
                    fiName = getJsonArrayValues(
                            caseId,
                            "preClearanceRequest",
                            "/financialInstruments/financialInstrumentsUnderlyingDistributionMore",
                            "/financialInstrument/bjbNameLongProduct"
                    );
                    valor = getJsonArrayValues(
                            caseId,
                            "preClearanceRequest",
                            "/financialInstruments/financialInstrumentsUnderlyingDistributionMore",
                            "/financialInstrument/valorNo"
                    );
                    isin = getJsonArrayValues(
                            caseId,
                            "preClearanceRequest",
                            "/financialInstruments/financialInstrumentsUnderlyingDistributionMore",
                            "/financialInstrument/isin"
                    );
                }
            }
            case "others" -> {
                fiName = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/financialInstrumentManual");
                valor = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/swissValorNumberManual");
                isin = getJsonValue(caseId, "preClearanceRequest", "/financialInstruments/ISINNumberManual");

            }
            default -> throw new IllegalArgumentException("Unknown financialInstrumentType: " + financialInstrumentType);
        }

        boolean fiManuallySelected = financialInstrumentType.equals("others");

        String action = getJsonValue(caseId, "captureOrderDetails", "/action");
        String quantityStr = getJsonValue(caseId, "captureOrderDetails", "/quantity");
        Double quantity = (quantityStr != null) ? Double.parseDouble(quantityStr) : null;

        boolean requestLimitOrder = "Yes".equalsIgnoreCase(getJsonValue(caseId, "captureOrderDetails", "/requestForLimitOrder"));

        boolean consentAdditionalRules   = "true".equals(getJsonValue(caseId, "consent", "/additionalRules"));
        boolean consentHoldingPeriods    = "true".equals(getJsonValue(caseId, "consent", "/holdingPeriods"));
        boolean consentFPA               = "true".equals(getJsonValue(caseId, "consent", "/fPA"));
        boolean consentAdHocConfArea     = "true".equals(getJsonValue(caseId, "consent", "/adHocConfArea"));
        boolean consentMNPILimitOrders   = "true".equals(getJsonValue(caseId, "consent", "/mNPILimitOrders"));
        boolean consentMNPIExposure      = "true".equals(getJsonValue(caseId, "consent", "/mNPIExposure"));
        boolean allConsentDone           = "true".equals(getJsonValue(caseId, "consent", "/allConsentDone"));

        String complianceDecision = getStringValue(caseId, "decisionLocalCompliance");
        if (complianceDecision == null) {
            complianceDecision = getStringValue(caseId, "decisionGlobalCompliance");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate complianceDecisionDate = parseDateVar(execution, "root.localComplianceDecisionDate", "root.globalComplianceDecisionDate", formatter);
        LocalDate lmDecisionDate = parseDateVar(execution, "root.lmDecisionDate", null, formatter);
        String lmDecision = getStringValue(caseId, "decisonLineManager");

        return PreClearanceRequestData.builder()
                .caseId(caseId)
                .createdOn(createdOn)
                .status(status)
                .requester(requester)
                .requestType(requestType)
                .employeeType(employeeType)
                .financialInstrumentType(financialInstrumentType)
                .fiName(fiName)
                .valor(valor)
                .isin(isin)
                .fiManuallySelected(fiManuallySelected)
                .action(action)
                .quantity(quantity)
                .requestLimitOrder(requestLimitOrder)
                .consentAdditionalRules(consentAdditionalRules)
                .consentHoldingPeriods(consentHoldingPeriods)
                .consentFPA(consentFPA)
                .consentAdHocConfArea(consentAdHocConfArea)
                .consentMNPILimitOrders(consentMNPILimitOrders)
                .consentMNPIExposure(consentMNPIExposure)
                .allConsentDone(allConsentDone)
                .complianceDecision(complianceDecision)
                .complianceDecisionDate(complianceDecisionDate)
                .lmDecision(lmDecision)
                .lmDecisionDate(lmDecisionDate)
                .build();
    }

    private String getStringValue(String caseId, String varName) {
        VariableInstance vi = cmmnRuntimeService.getVariableInstance(caseId, varName);
        return vi != null ? vi.getValue().toString() : null;
    }

    private String getJsonValue(String caseId, String varName, String path) {
        VariableInstance vi = cmmnRuntimeService.getVariableInstance(caseId, varName);
        JsonNode node = (vi != null) ? (JsonNode) vi.getValue() : null;
        return (node != null) ? node.at(path).asText(null) : null;
    }

    private String getJsonArrayValues(String caseId, String varName, String arrayPath, String innerPath) {
        VariableInstance vi = cmmnRuntimeService.getVariableInstance(caseId, varName);
        JsonNode root = (vi != null) ? (JsonNode) vi.getValue() : null;

        if (root == null) {
            return null;
        }

        JsonNode arrayNode = root.at(arrayPath);
        if (!arrayNode.isArray()) {
            return null;
        }

        List<String> values = new ArrayList<>();
        for (JsonNode element : arrayNode) {
            JsonNode valueNode = element.at(innerPath);
            if (!valueNode.isMissingNode() && !valueNode.isNull()) {
                values.add(valueNode.asText());
            }
        }
        return String.join(", ", values);
    }

    private LocalDate parseDateVar(DelegateExecution e, String localVar, String globalVar, DateTimeFormatter formatter) {
        Object dateObj = e.getVariable(localVar);
        if (dateObj == null && globalVar != null) {
            dateObj = e.getVariable(globalVar);
        }
        return (dateObj != null) ? LocalDate.parse(dateObj.toString(), formatter) : null;
    }
}

