package com.juliusbaer.cmt.pat.indexing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.indexing.api.IndexingJsonConstants;
import com.flowable.indexing.api.IndexingManagerHelper;
import com.flowable.indexing.impl.IndexedDataEnhancerAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PcrIndexedDataEnhancer extends IndexedDataEnhancerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PcrIndexedDataEnhancer.class);

    private final ObjectMapper objectMapper;

    public static Optional<String> asString(JsonNode node) {
        return Optional.ofNullable(node).filter(JsonNode::isTextual).map(JsonNode::asText).filter(StringUtils::isNoneBlank);
    }

    private boolean isMatchingCaseDefinitionKey(ObjectNode data, String caseDefinitionValueKey) {
        return asString(data.get(caseDefinitionValueKey)).map(this::isMatchingCase).orElse(false);
    }

    private boolean isMatchingCase(String caseDefinitionKey) {
        return caseDefinitionKey != null && PcrCaseVariableConstants.CASE_DEFINITION_KEYS.contains(caseDefinitionKey);
    }

    @Override
    public void enhanceHistoricVariableReindexData(HistoricVariableInstance historicVariableInstance, String scopeId, String scopeType, String scopeHierarchyType, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        try {
            if (ScopeTypes.CMMN.equals(scopeType) && !isMatchingCaseDefinitionKey(data, "caseDefinitionKey")) {
                return;
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("ReIndexing historic variable instance {}, name {}, scopeId {}, scopeType {}", historicVariableInstance.getId(), historicVariableInstance.getVariableName(), scopeId, scopeType);
            }
            enhanceVariableData(data, historicVariableInstance.getVariableName(), historicVariableInstance.getValue(), scopeHierarchyType);
        } catch (Exception e) {
            LOGGER.error("Failed to ReIndex historic variable instance scopeId {}, scopeType {}", scopeId, scopeType, e);
        }
    }

    @Override
    public void enhanceTaskReindexData(HistoricTaskInstance historicTaskInstance, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {

        try {
            LOGGER.debug("ReIndexing historic task instance {}, name {}, scopeID {}, scopeType {}", historicTaskInstance.getId(), historicTaskInstance.getName(), historicTaskInstance.getScopeId(), historicTaskInstance.getScopeType());
            handleTaskVariable(data, PcrTaskVariableConstants.FIELD_CREATOR_USER_ROLE,
                    PcrTaskIndexConstants.FIELD_CREATOR_USER_ROLE, objectMapper);

            handleTaskVariable(data, PcrTaskVariableConstants.FIELD_TEAM_ID,
                    PcrTaskIndexConstants.FIELD_TEAM_ID, objectMapper);
        } catch (Exception e) {
            LOGGER.error("Failed to ReIndex task instance {}, name {}, scopeID {}, scopeType {}", historicTaskInstance.getId(), historicTaskInstance.getName(), historicTaskInstance.getScopeId(), historicTaskInstance.getScopeType(), e);
        }
    }

    private void handleTaskVariable(JsonNode data, String variableFieldName, String indexFieldName, ObjectMapper objectMapper) {
        JsonNode variables = data.get("variables");
        if (variables != null && variables.has(variableFieldName)) {
            JsonNode variableNode = variables.get(variableFieldName);
            String value = variableNode.isTextual() ? variableNode.asText() : variableNode.toString();
            ((ObjectNode) data).set(indexFieldName, objectMapper.convertValue(value, JsonNode.class));
        }
    }

    protected void enhanceVariableData(ObjectNode data, String variableName, Object variableValue, String scopeHierarchyType) {
        String mappingType = data.get(IndexingJsonConstants.PROPERTY_MAPPING_TYPE).textValue();
        if (IndexingJsonConstants.MAPPING_TYPE_CASE_INSTANCE.equals(mappingType)) {
            switch (variableName) {
                case PcrCaseVariableConstants.FIELD_CASE_SEQUENCE_VALUE:
                    handleVariable(PcrCaseIndexConstants.FIELD_CASE_SEQUENCE_VALUE, data, variableValue);
                    break;
                case PcrCaseVariableConstants.FIELD_PRECLEARANCE_REQUEST:
                    handleFinancialInstruments(((ObjectNode) variableValue).get(PcrCaseVariableConstants.FIELD_FINANCIAL_INSTRUMENTS), data);
                    break;
                case PcrCaseVariableConstants.FIELD_OWNER_NAME:
                    handleVariable(PcrCaseIndexConstants.FIELD_OWNER_NAME, data, variableValue);
                    break;
                case PcrCaseVariableConstants.FIELD_CREATOR_USER_ROLE:
                    handleVariable(PcrCaseIndexConstants.FIELD_CREATOR_USER_ROLE, data, variableValue);
                    break;
                case PcrCaseVariableConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE:
                    handleVariable(PcrCaseIndexConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE, data, variableValue);
                    break;
                case PcrCaseVariableConstants.FIELD_CONSENT:
                    handleVariable(PcrCaseIndexConstants.FIELD_ALL_CONSENTS, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_ALL_CONSENTS));
                    handleVariable(PcrCaseIndexConstants.FIELD_ADDITIONAL_RULES, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_ADDITIONAL_RULES));
                    handleVariable(PcrCaseIndexConstants.FIELD_HOLDING_PERIODS, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_HOLDING_PERIODS));
                    handleVariable(PcrCaseIndexConstants.FIELD_FPA, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_FPA));
                    handleVariable(PcrCaseIndexConstants.FIELD_ADHOC_CONFAREA, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_ADHOC_CONFAREA));
                    handleVariable(PcrCaseIndexConstants.FIELD_MNPIL_LIMIT_ORDERS, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_MNPIL_LIMIT_ORDERS));
                    handleVariable(PcrCaseIndexConstants.FIELD_MNPI_EXPOSURE, data, getYesNoValue(data, PcrCaseVariableConstants.FIELD_MNPI_EXPOSURE));
                    break;
                case PcrCaseVariableConstants.FIELD_ORDER_DETAILS:
                    handleVariable(PcrCaseIndexConstants.FIELD_ACTION, data, StringUtils.capitalize(((ObjectNode) variableValue).get(PcrCaseVariableConstants.FIELD_ACTION).asText("")));
                    handleVariable(PcrCaseIndexConstants.FIELD_QUANTITY, data, ((ObjectNode) variableValue).get(PcrCaseVariableConstants.FIELD_QUANTITY));
                    handleVariable(PcrCaseIndexConstants.FIELD_REQUEST_LIMIT_ORDER, data, StringUtils.capitalize(((ObjectNode) variableValue).get(PcrCaseVariableConstants.FIELD_REQUEST_LIMIT_ORDER).asText("")));
                    break;
                case PcrCaseVariableConstants.FIELD_LM_DECISION:
                    handleVariable(PcrCaseIndexConstants.FIELD_LM_DECISION, data, variableValue);
                    break;
                default:
                    break;
            }
        }
        if (IndexingJsonConstants.MAPPING_TYPE_TASK.equals(mappingType)) {
            switch (variableName) {
                case PcrTaskVariableConstants.FIELD_TEAM_ID:
                    handleVariable(PcrTaskIndexConstants.FIELD_TEAM_ID, data, variableValue);
                    break;
                case PcrTaskVariableConstants.FIELD_CREATOR_USER_ROLE:
                    handleVariable(PcrTaskIndexConstants.FIELD_CREATOR_USER_ROLE, data, variableValue);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleFinancialInstruments(JsonNode instruments, ObjectNode data) {
        if (instruments == null) {
            return;
        }

        handleVariable(PcrCaseIndexConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE, data, instruments.get(PcrCaseVariableConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE));

        if (instruments.get(PcrCaseVariableConstants.FIELD_FINANCIAL_INSTRUMENT_TYPE).equals("others")){
            handleVariable(PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED, data, PcrCaseIndexConstants.VARIABLE_VALUE_YES);
        } else {
            handleVariable(PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED, data, PcrCaseIndexConstants.VARIABLE_VALUE_NO);
        }

        List<String> issuers = new ArrayList<>();
        List<String> isins = new ArrayList<>();
        List<String> valors = new ArrayList<>();

        JsonNode financialInstrumentsList = instruments.get(PcrCaseVariableConstants.FIELD_FINANCIAL_INSTRUMENTS_LIST);
        JsonNode financialInstrument = instruments.get(PcrCaseVariableConstants.FIELD_FINANCIAL_INSTRUMENT);

        if (financialInstrumentsList != null && financialInstrumentsList.isArray())
        {
            for (JsonNode node : financialInstrumentsList) {
                JsonNode fiNode = node.path(PcrCaseVariableConstants.FIELD_FI);

                if (fiNode.hasNonNull(PcrCaseVariableConstants.FIELD_FI_NAME)) {
                    issuers.add(fiNode.get(PcrCaseVariableConstants.FIELD_FI_NAME).asText());
                }

                if (fiNode.hasNonNull(PcrCaseVariableConstants.FIELD_ISIN)) {
                    isins.add(fiNode.get(PcrCaseVariableConstants.FIELD_ISIN).asText());
                }

                if (fiNode.hasNonNull(PcrCaseVariableConstants.FIELD_VALOR)) {
                    valors.add(fiNode.get(PcrCaseVariableConstants.FIELD_VALOR).asText());
                }
            }
        }
        else if(financialInstrumentsList != null && !financialInstrumentsList.isMissingNode())
        {

            if (financialInstrumentsList.hasNonNull(PcrCaseVariableConstants.FIELD_FI_NAME)) {
                issuers.add(financialInstrumentsList.get(PcrCaseVariableConstants.FIELD_FI_NAME).asText());
            }

            if (financialInstrumentsList.hasNonNull(PcrCaseVariableConstants.FIELD_ISIN)) {
                isins.add(financialInstrumentsList.get(PcrCaseVariableConstants.FIELD_ISIN).asText());
            }

            if (financialInstrumentsList.hasNonNull(PcrCaseVariableConstants.FIELD_VALOR)) {
                valors.add(financialInstrumentsList.get(PcrCaseVariableConstants.FIELD_VALOR).asText());
            }
            handleVariable(PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED, data, PcrCaseIndexConstants.VARIABLE_VALUE_YES);

        }
        else  if (financialInstrument != null && !financialInstrument.isMissingNode())
        {

            if (financialInstrument.hasNonNull(PcrCaseVariableConstants.FIELD_FI_NAME)) {
                issuers.add(financialInstrument.get(PcrCaseVariableConstants.FIELD_FI_NAME).asText());
            }

            if (financialInstrument.hasNonNull(PcrCaseVariableConstants.FIELD_ISIN)) {
                isins.add(financialInstrument.get(PcrCaseVariableConstants.FIELD_ISIN).asText());
            }

            if (financialInstrument.hasNonNull(PcrCaseVariableConstants.FIELD_VALOR)) {
                valors.add(financialInstrument.get(PcrCaseVariableConstants.FIELD_VALOR).asText());
            }
        }
        else {
            LOGGER.error("ERROR NO FI SELECTED !");
        }


        if (!issuers.isEmpty()) {
            data.set(PcrCaseIndexConstants.FIELD_FI_NAME, objectMapper.convertValue(String.join(", ", issuers), JsonNode.class));
        }

        if (!isins.isEmpty()) {
            data.set(PcrCaseIndexConstants.FIELD_ISIN, objectMapper.convertValue(String.join(", ", isins), JsonNode.class));
        }

        if (!valors.isEmpty()) {
            data.set(PcrCaseIndexConstants.FIELD_VALOR, objectMapper.convertValue(String.join(", ", valors), JsonNode.class));
        }
    }

    private void handleVariable(String indexField, ObjectNode data, Object variableValue) {
        data.set(indexField, variableValue == null ? null : objectMapper.convertValue(variableValue, JsonNode.class));
    }

    private String getYesNoValue(ObjectNode variableValue, String fieldName) {
        return PcrCaseIndexConstants.VARIABLE_VALUE_TRUE.equalsIgnoreCase(variableValue.path(fieldName).asText()) ? PcrCaseIndexConstants.VARIABLE_VALUE_YES : PcrCaseIndexConstants.VARIABLE_VALUE_NO;
    }
}
