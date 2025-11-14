package com.juliusbaer.cmt.pat.jbEntities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.FinancialInstrumentIdentifier;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.ValidateComplianceJbInstrumentRequest;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.ValidateComplianceJbInstrumentResponse;
import com.juliusbaer.cmt.pat.jbEntities.builder.FinancialInstrumentIdentifierBuilder;
import com.juliusbaer.cmt.pat.jbEntities.builder.ValidateComplianceJbInstrumentRequestBuilder;
import org.flowable.engine.delegate.BpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("jbEntitiesValidation")
@SuppressWarnings("unused") // Used by the model
public class JbEntitiesValidation {

    private static final Logger LOG = LoggerFactory.getLogger(JbEntitiesValidation.class);

    // Constants for JSON field names to avoid "magic strings"
    private static final String FIELD_FINANCIAL_INSTRUMENT_TYPE = "financialInstrumentType";
    private static final String FIELD_FINANCIAL_INSTRUMENT = "financialInstrument";
    private static final String FIELD_FINANCIAL_INSTRUMENTS_LIST = "financialInstrumentsList";
    private static final String TYPE_SHARES = "shares";
    private static final String TYPE_UNDERLYING = "underlying";
    private static final String SERVICE_UNAVAILABLE_ERROR = "SERVICE_UNAVAILABLE";
    private static final String SERVICE_TIMEOUT_ERROR = "SERVICE_TIMEOUT";
    private static final String UNKNOWN_VALIDATION_ERROR = "UNKNOWN_VALIDATION_ERROR";
    private final ObjectMapper objectMapper;
    private final DefaultComplianceJbInstrumentService complianceService;

    public JbEntitiesValidation(ObjectMapper objectMapper, DefaultComplianceJbInstrumentService complianceService) {
        this.objectMapper = objectMapper;
        this.complianceService = complianceService;
    }

    public Boolean buildAndValidate(JsonNode payload) throws JsonProcessingException {
        try {
            LOG.info("Received payload for validation: {}", payload);

            ValidateComplianceJbInstrumentRequest request = buildRequestFromPayload(payload);

            if (request == null) {
                LOG.warn("Could not build a valid request from the payload. Unsupported type or invalid structure.");
                return false;
            }

            LOG.info("Sending validation request: {}", objectMapper.writeValueAsString(request));
            ValidateComplianceJbInstrumentResponse serviceResponse = complianceService.getValidateComplianceJbInstrument(request);
            LOG.info("Receiving validation response: {}", objectMapper.writeValueAsString(serviceResponse));
            return serviceResponse.getIsInComplianceList();
        } catch (HttpServerErrorException ex) {
            // This catches 5xx errors from the server.
            LOG.error("Validation Service is unavailable (HTTP {}): {}", ex.getStatusCode(), ex.getMessage());
            throw new BpmnError("SERVICE_UNAVAILABLE", "Rule engine service returned a server error.");

        } catch (ResourceAccessException ex) {
            // This is often a sign of a timeout or network problem.
            LOG.error("Validation Service timed out or is unreachable: {}", ex.getMessage());
            throw new BpmnError("SERVICE_TIMEOUT", "Timeout while contacting the rule engine service.");

        } catch (Exception ex) {
            // A general catch-all for any other unexpected problem.
            LOG.error("An unexpected error occurred during FI validation: {}", ex.getMessage());
            throw new BpmnError("UNKNOWN_VALIDATION_ERROR", "An unexpected error occurred during validation.");
        }
    }

    private ValidateComplianceJbInstrumentRequest buildRequestFromPayload(JsonNode payload) {
        String instrumentType = payload.path(FIELD_FINANCIAL_INSTRUMENT_TYPE).asText();

        switch (instrumentType) {
            case TYPE_SHARES:
                LOG.info("Building request for single financial ithat nstrument (shares).");
                JsonNode instrumentNode = payload.path(FIELD_FINANCIAL_INSTRUMENT);
                return buildRequestForSingleInstrument(instrumentNode);

            case TYPE_UNDERLYING:
                LOG.info("Building request for a list of underlyings.");
                JsonNode underlyingsNode = payload.path(FIELD_FINANCIAL_INSTRUMENTS_LIST);
                if (underlyingsNode.isArray()) {
                    return buildRequestForUnderlyings((ArrayNode) underlyingsNode);
                }
                LOG.error("Expected '{}' to be an array, but it was not.", FIELD_FINANCIAL_INSTRUMENTS_LIST);
                return null;

            default:
                LOG.warn("Unsupported financialInstrumentType: '{}'", instrumentType);
                return null;
        }
    }

    private ValidateComplianceJbInstrumentRequest buildRequestForSingleInstrument(JsonNode instrumentNode) {
        FinancialInstrumentIdentifier identifier = createIdentifierFromJson(instrumentNode);
        return new ValidateComplianceJbInstrumentRequestBuilder().withFinancialInstrument(identifier).build();
    }

    private ValidateComplianceJbInstrumentRequest buildRequestForUnderlyings(ArrayNode underlyingsArray) {
        List<FinancialInstrumentIdentifier> identifiers = StreamSupport.stream(underlyingsArray.spliterator(), false).map(underlying -> underlying.path(FIELD_FINANCIAL_INSTRUMENT)).map(this::createIdentifierFromJson).collect(Collectors.toList());

        return ValidateComplianceJbInstrumentRequestBuilder.builder().withUnderlyings(identifiers).build();
    }

    private FinancialInstrumentIdentifier createIdentifierFromJson(JsonNode instrumentNode) {
        return FinancialInstrumentIdentifierBuilder.builder().withIsin(getTextValue(instrumentNode, "isin")).withBbgid(getTextValue(instrumentNode, "bbgid")).withJbvalor(getTextValue(instrumentNode, "valorNo")).withJbgid(getTextValue(instrumentNode, "jbGlobalId")).build();
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        return field.isNull() || field.isMissingNode() ? null : field.asText();
    }
}