package com.juliusbaer.cmt.pat.preClearance.validatePreClearance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.*;
import com.juliusbaer.cmt.pat.logging.Loggers;
import com.juliusbaer.cmt.pat.preClearance.validatePreClearance.builder.FinancialInstrumentBuilder;
import com.juliusbaer.cmt.pat.preClearance.validatePreClearance.builder.UnderlyingBuilder;
import com.juliusbaer.cmt.pat.preClearance.validatePreClearance.builder.ValidatePreClearanceResquestBuilder;
import org.flowable.engine.delegate.BpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("preClearanceValidation")
public class PreClearanceValidation {

    private static final Logger LOG = LoggerFactory.getLogger(PreClearanceValidation.class);
    private static final String SERVICE_UNAVAILABLE_ERROR = "SERVICE_UNAVAILABLE";
    private static final String SERVICE_TIMEOUT_ERROR = "SERVICE_TIMEOUT";
    private static final String UNKNOWN_VALIDATION_ERROR = "UNKNOWN_VALIDATION_ERROR";

    private final ObjectMapper objectMapper;
    private final DefaultPreClearanceService defaultPreClearanceService;

    private static final Logger SVL = Loggers.SVL;
    private static final Logger ATL = Loggers.ATL;

    public PreClearanceValidation(ObjectMapper objectMapper, DefaultPreClearanceService defaultPreClearanceService) {
        this.objectMapper = objectMapper;
        this.defaultPreClearanceService = defaultPreClearanceService;
    }

    public JsonNode buildAndValidate(String requestedBy, String requestedFor, JsonNode financialInstruments,  JsonNode captureDetails) {
        LOG.info("Received raw financialInstruments: {}", financialInstruments);

        // captureDetails mapping

        String action =captureDetails.get("action").asText();
        ValidatePreClearanceRequest.TransactionTypeEnum transactionType;
        try {
            transactionType = getValidatedTransactionType(action);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid action value provided: {}", action, e);
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("error", "Invalid transaction type");
            errorNode.put("details", e.getMessage());
            return errorNode;
        }

        ArrayNode financialInstrumentsArray = parseFinancialInstruments(financialInstruments);
        ArrayNode underlyingsArray = parseUnderlyings(financialInstruments);

        LOG.info("Prepared financialInstrumentsArray: {}", financialInstrumentsArray);
        LOG.info("Prepared underlying: {}", underlyingsArray);

        ArrayNode underlyingResponses = objectMapper.createArrayNode();
        ObjectNode out = objectMapper.createObjectNode();

        // If requestedFor is not provided, default to requestedBy
        String finalRequestedFor = Optional.ofNullable(requestedFor).orElse(requestedBy);

        // Process financial instruments
        for (JsonNode fiWrap : financialInstrumentsArray) {
            FinancialInstrument fi = buildFinancialInstrument(fiWrap);
            ValidatePreClearanceResquestBuilder builder = ValidatePreClearanceResquestBuilder.builder().requestedBy(requestedBy).requestedFor(finalRequestedFor).financialInstrument(fi).transactionType(transactionType);

            boolean isRequestForLimitOrder = captureDetails != null && captureDetails.has("requestForLimitOrder") && captureDetails.get("requestForLimitOrder").asText().equals("yes");

            builder = setLimitOrder(captureDetails, isRequestForLimitOrder, builder);
            ValidatePreClearanceRequest request = builder.build();

            try {
                LOG.info("Validating Financial Instrument with request: {}", objectMapper.writeValueAsString(request));
                ValidatePreClearanceResponse serviceResp = defaultPreClearanceService.getValidatePreClearance(request);
                LOG.info("Raw Financial Instrument Service Response: {}", objectMapper.writeValueAsString(serviceResp));
                out.set("fiResponse", objectMapper.valueToTree(serviceResp));
                LOG.info("ValidatePreClearanceResponse: {}", out);
            } catch (Exception ex) {
                handleServiceExceptions(ex);
            }
        }

        // Process underlyings, only if a financial instrument was processed
        for (JsonNode ulWrap : underlyingsArray) {
            List<Underlying> underlying = buildUnderlyings(ulWrap);
            ValidatePreClearanceResquestBuilder builder = ValidatePreClearanceResquestBuilder.builder().requestedBy(requestedBy).requestedFor(finalRequestedFor).underlyings(underlying).transactionType(transactionType);

            boolean isRequestForLimitOrder = captureDetails != null && captureDetails.has("requestForLimitOrder") && captureDetails.get("requestForLimitOrder").asText().equals("yes");

            builder= setLimitOrder(captureDetails, isRequestForLimitOrder, builder);
            ValidatePreClearanceRequest request = builder.build();

            try {
                LOG.info("Validating Underlying with request: {}", objectMapper.writeValueAsString(request));
                Object serviceResp = defaultPreClearanceService.getValidatePreClearance(request);
                out.set("underlyingResponses", objectMapper.valueToTree(serviceResp));
                LOG.info("Raw Underlying Service Response: {}", objectMapper.writeValueAsString(serviceResp));
            } catch (Exception ex) {
                handleServiceExceptions(ex);
            }
        }

        return out;
    }

    private ValidatePreClearanceResquestBuilder setLimitOrder(JsonNode captureDetails, boolean isRequestForLimitOrder, ValidatePreClearanceResquestBuilder builder) {
        if (isRequestForLimitOrder) {
            builder.limitOrder(true).volumeSize(captureDetails.get("quantity").asInt()).validityDateFrom(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).validityDateTo(captureDetails.get("limitOrderValidityDate").asText());
        } else {
            builder.limitOrder(false);
        }
        return builder;
    }

    private void handleServiceExceptions(Exception ex) {
        if (ex instanceof HttpServerErrorException hsee) {
            LOG.error("Validation Service is unavailable (HTTP {}): {}", hsee.getStatusCode(), hsee.getMessage());
            throw new BpmnError(SERVICE_UNAVAILABLE_ERROR, "Rule engine service returned a server error.");
        } else if (ex instanceof ResourceAccessException) {
            LOG.error("Validation Service timed out or is unreachable: {}", ex.getMessage());
            throw new BpmnError(SERVICE_TIMEOUT_ERROR, "Timeout while contacting the rule engine service.");
        } else {
            LOG.error("An unexpected error occurred during validation: {}", ex.getMessage());
            throw new BpmnError(UNKNOWN_VALIDATION_ERROR, "An unexpected error occurred during validation.");
        }
    }

    private ValidatePreClearanceRequest.TransactionTypeEnum getValidatedTransactionType(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action value cannot be null or empty.");
        }
        return ValidatePreClearanceRequest.TransactionTypeEnum.fromValue(action);
    }

    // The parsing and building methods remain largely the same, but are included for completeness.
    private ArrayNode parseFinancialInstruments(JsonNode input) {
        ArrayNode array = objectMapper.createArrayNode();
        if (input == null || input.isNull()) return array;
        JsonNode financialInstrument = input.get("financialInstrument");
        if (financialInstrument == null || financialInstrument.isNull()) return array;

        if (input.has("financialInstrument")) {
            array.add(input.get("financialInstrument"));
        } else if (input.isObject()) {
            array.add(input);
        }
        return array;
    }

    private ArrayNode parseUnderlyings(JsonNode input) {
        ArrayNode array = objectMapper.createArrayNode();
        if (input == null || input.isNull()) return array;

        if (input.has("financialInstrumentsList")) {
            array.add(input.get("financialInstrumentsList"));
        } else if (input.isObject()) {
            return array;
        }

        /*if (input.has("distribution")) {
            JsonNode distributionNode = input.get("financialInstrumentsUnderlyingDistributionMore");
            if (distributionNode != null && distributionNode.isArray()) {
                array.addAll((ArrayNode) distributionNode);
            } else if (distributionNode != null) {
                array.add(distributionNode);
            }
        } else if (input.has("financialInstrumentsList")) {
            array.add(input.get("financialInstrumentsList"));
        } else if (input.isObject()) {
            array.add(input);
        } */
        return array;
    }

    private FinancialInstrument buildFinancialInstrument(JsonNode fiWrap) {
        JsonNode fiNode = fiWrap.has("financialInstrument") ? fiWrap.get("financialInstrument") : fiWrap;

        FinancialInstrumentIdentifier fiIdent = new FinancialInstrumentIdentifier();
        JsonNode isinNode = fiNode.get("isin");
        fiIdent.setIsin(isinNode != null && !isinNode.isNull() ? isinNode.asText() : null);
        JsonNode valorNode = fiNode.get("valorNo");
        fiIdent.setValor(valorNode != null && !valorNode.isNull() ? valorNode.asText() : null);
        JsonNode bbgNode = fiNode.get("bbgId");
        fiIdent.setBbgid(bbgNode != null && !bbgNode.isNull() ? bbgNode.asText() : null);
        JsonNode jbgNode = fiNode.get("jbGlobalId");
        fiIdent.setJbgid(jbgNode != null && !jbgNode.isNull() ? jbgNode.asText() : null);
        return new FinancialInstrumentBuilder().identifier(fiIdent).build();
    }

    private List<Underlying> buildUnderlyings(JsonNode ulWrap) {
        List<Underlying> underlyings = new ArrayList<>();
        if (ulWrap != null) {
            for (JsonNode element : ulWrap) {
                FinancialInstrument financialInstrument = this.buildFinancialInstrument(element);
                UnderlyingBuilder underlyingBuilder = new UnderlyingBuilder().identifier(financialInstrument.getIdentifier());
                underlyings.add(underlyingBuilder.build());
            }
            return underlyings;
        }
        return underlyings;
    }
}