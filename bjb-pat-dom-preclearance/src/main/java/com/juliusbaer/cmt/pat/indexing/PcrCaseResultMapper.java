package com.juliusbaer.cmt.pat.indexing;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowable.platform.service.caze.CaseInstanceResultMapper;
import com.flowable.platform.service.caze.CaseInstanceSearchRepresentation;

@Service
public class PcrCaseResultMapper implements CaseInstanceResultMapper.Enhancer {

    @Override
    public void enhance(CaseInstanceSearchRepresentation response, JsonNode sourceNode) {
        if (sourceNode.get("startTime") != null) {
            ZonedDateTime dateTime = ZonedDateTime.parse(sourceNode.get("startTime").asText());
            String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
            response.getVariables().put(PcrCaseIndexConstants.FIELD_START_TIME, formatted);
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_ACTION) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_ACTION, StringUtils.capitalize(sourceNode.get(PcrCaseIndexConstants.FIELD_ACTION).asText()));
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_FI_NAME) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_FI_NAME, sourceNode.get(PcrCaseIndexConstants.FIELD_FI_NAME).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_VALOR) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_VALOR, sourceNode.get(PcrCaseIndexConstants.FIELD_VALOR).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_ISIN) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_ISIN, sourceNode.get(PcrCaseIndexConstants.FIELD_ISIN).asText());
        }
        if(sourceNode.get(PcrCaseIndexConstants.FIELD_QUANTITY) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_QUANTITY, sourceNode.get(PcrCaseIndexConstants.FIELD_QUANTITY).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_REQUEST_LIMIT_ORDER) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_REQUEST_LIMIT_ORDER, sourceNode.get(PcrCaseIndexConstants.FIELD_REQUEST_LIMIT_ORDER).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE, sourceNode.get(PcrCaseIndexConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED, sourceNode.get(PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_ALL_CONSENTS) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_ALL_CONSENTS, sourceNode.get(PcrCaseIndexConstants.FIELD_ALL_CONSENTS).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_ADDITIONAL_RULES) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_ADDITIONAL_RULES, sourceNode.get(PcrCaseIndexConstants.FIELD_ADDITIONAL_RULES).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_HOLDING_PERIODS) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_HOLDING_PERIODS, sourceNode.get(PcrCaseIndexConstants.FIELD_HOLDING_PERIODS).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_FPA) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_FPA, sourceNode.get(PcrCaseIndexConstants.FIELD_FPA).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_ADHOC_CONFAREA) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_ADHOC_CONFAREA, sourceNode.get(PcrCaseIndexConstants.FIELD_ADHOC_CONFAREA).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_MNPIL_LIMIT_ORDERS) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_MNPIL_LIMIT_ORDERS, sourceNode.get(PcrCaseIndexConstants.FIELD_MNPIL_LIMIT_ORDERS).asText());
        }
        if (sourceNode.get(PcrCaseIndexConstants.FIELD_MNPI_EXPOSURE) != null) {
            response.getVariables().put(PcrCaseIndexConstants.FIELD_MNPI_EXPOSURE, sourceNode.get(PcrCaseIndexConstants.FIELD_MNPI_EXPOSURE).asText());
        }
    }
}
