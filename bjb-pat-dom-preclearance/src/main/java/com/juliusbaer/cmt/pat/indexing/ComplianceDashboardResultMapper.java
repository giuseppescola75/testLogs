package com.juliusbaer.cmt.pat.indexing;

import static com.juliusbaer.cmt.pat.indexing.PcrCaseIndexConstants.FIELD_OWNER_NAME;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.flowable.indexing.utils.ElasticsearchResultConverter;
import com.flowable.platform.common.util.JsonUtil;
import com.flowable.platform.service.BaseJsonMapper;

@Service
public class ComplianceDashboardResultMapper extends BaseJsonMapper<ComplianceDashboardCaseInstanceRepresentation>
        implements ElasticsearchResultConverter.ResultMapper<ComplianceDashboardCaseInstanceRepresentation> {

    public ComplianceDashboardResultMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public ComplianceDashboardCaseInstanceRepresentation convert(JsonNode jsonNode) {

        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> parent = new HashMap<>();
        flattenVariables(jsonNode, variables, parent, root);

        Map<String, Object> preClearanceRequest = convertToMap((ObjectNode) variables.get("preClearanceRequest"));

        ComplianceDashboardCaseInstanceRepresentation result = ComplianceDashboardCaseInstanceRepresentation.builder()
                .id(JsonUtil.getString(jsonNode, "id"))
                .ownerName(JsonUtil.getString(jsonNode, FIELD_OWNER_NAME))
                .businessKey(JsonUtil.getString(jsonNode, "businessKey"))
                .businessStatusLabel(JsonUtil.getString(jsonNode, "businessStatusLabel"))
                .started(JsonUtil.getDate(jsonNode, "startTime"))
                .quantity(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_QUANTITY))
                .fiName(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_FI_NAME))
                .valor(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_VALOR))
                .isin(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_ISIN))
                .allConsentsProvided(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_ALL_CONSENTS))
                .requestLimitOrder(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_REQUEST_LIMIT_ORDER))
                .fiManuallySelected(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_FI_MANUALLY_SELECTED))
                .action(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_ACTION))
                .lmDecision(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_LM_DECISION))
                .lmDecisionDate(JsonUtil.getDate(jsonNode, PcrCaseIndexConstants.FIELD_LM_DECISION_DATE))
                .complianceDecision(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_COMPLIANCE_DECISION))
                .complianceDecisionDate(JsonUtil.getDate(jsonNode, PcrCaseIndexConstants.FIELD_COMPLIANCE_DECISION_DATE))
                .additionalRules(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_ADDITIONAL_RULES))
                .holdingPeriods(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_HOLDING_PERIODS))
                .fpa(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_FPA))
                .mnpiExposure(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_MNPI_EXPOSURE))
                .mnpiLimitOrders(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_MNPIL_LIMIT_ORDERS))
                .adhocConfarea(JsonUtil.getString(jsonNode, PcrCaseIndexConstants.FIELD_ADHOC_CONFAREA))
                .build();

        return result;
    }

    private String getOrEmpty(Map<String, Object> variables, String variableName) {
        Object variable = variables.get(variableName);
        if (variable instanceof TextNode) {
            return ((TextNode) variable).asText();
        } else {
            return "";
        }
    }

    private Map<String, Object> convertToMap(ObjectNode node) {
        if(node == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(node, new TypeReference<Map<String, Object>>() {

        });
        return map;
    }
}
