package com.juliusbaer.cmt.pat.dedicatedRM.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.dataobject.api.runtime.DataObjectInstanceVariableContainer;
import com.flowable.dataobject.api.runtime.DataObjectRuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hibernate.engine.config.spi.StandardConverters.asString;

@Service("dedicatedRmSimpleService")
@RequiredArgsConstructor
public class DedicatedRmExtactIdsService {

    private final DataObjectRuntimeService dataObjectRuntimeService;
    private static final String EXB_MEMBER_RELATIONSHIP_KEY = "PCR_DO002";
    private final DedicatedRMService dedicatedRMService;
    private static final String TENANT_ID = "bjb-global";
    private final ObjectMapper objectMapper = new ObjectMapper();


    public ObjectNode getSimpleRmInfo(String exbMemberUserId) {

        ObjectNode complexData = dedicatedRMService.fetchByDedicated(exbMemberUserId, null);

        ObjectNode simpleResponse = objectMapper.createObjectNode();

        ObjectNode dedicatedRmNode = objectMapper.createObjectNode();
        JsonNode dedicatedId = complexData.get("id");
        JsonNode dedicatedEmail = complexData.get("email");

        if (dedicatedId != null && dedicatedId.isTextual()) {
            dedicatedRmNode.set("id", dedicatedId);
        }

        if (dedicatedEmail != null && dedicatedEmail.isTextual()) {
            dedicatedRmNode.set("email", dedicatedEmail);
        }

        simpleResponse.set("dedicatedRm", dedicatedRmNode);

        ArrayNode simpleDeputiesArray = objectMapper.createArrayNode();
        JsonNode complexDeputiesArray = complexData.get("deputies");

        if (complexDeputiesArray != null && complexDeputiesArray.isArray()) {
            for (JsonNode deputy : complexDeputiesArray) {
                ObjectNode simpleDeputy = objectMapper.createObjectNode();

                JsonNode deputyId = deputy.get("id");
                JsonNode deputyEmail = deputy.get("email");

                if (deputyId != null && deputyId.isTextual()) {
                    simpleDeputy.set("id", deputyId);
                }
                if (deputyEmail != null && deputyEmail.isTextual()) {
                    simpleDeputy.set("email", deputyEmail);
                }
                simpleDeputiesArray.add(simpleDeputy);
            }
        }
        simpleResponse.set("deputies", simpleDeputiesArray);

        return simpleResponse;
    }


    public ObjectNode getAggregatedRmCollections(String exbMemberUserId) {

        if (exbMemberUserId == null) {
            ObjectNode emptyOutput = objectMapper.createObjectNode();
            emptyOutput.set("allRmIds", objectMapper.createArrayNode());
            emptyOutput.set("allRmEmails", objectMapper.createArrayNode());
            return emptyOutput;
        }

        List<Map<String, Object>> filteredDataItems = dataObjectRuntimeService.createDataObjectInstanceQuery()
                .definitionKey(EXB_MEMBER_RELATIONSHIP_KEY)
                .tenantId(TENANT_ID)
                .value("EXB_MEMBER_USER_ID_", exbMemberUserId) // Filtro a livello di query
                .operation("findByExbMemberUserId")
                .list()
                .stream()
                .map(DataObjectInstanceVariableContainer::getData)
                .filter(data -> {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    Object rowExbId = dataMap.get("EXB_MEMBER_USERID_");
                    return exbMemberUserId.equals(asString(rowExbId));
                })
                .map(data -> (Map<String, Object>) data) // Cast per il passaggio successivo
                .collect(Collectors.toList());

        Set<String> uniqueIds = new HashSet<>();
        Set<String> uniqueEmails = new HashSet<>();

        for (Map<String, Object> item : filteredDataItems) {

            Object emailObj = item.get("RM_EMAIL_");
            if (emailObj != null) {
                uniqueEmails.add(emailObj.toString());
            }

            Object idObj = item.get("RM_USER_ID_");
            if (idObj != null) {
                uniqueIds.add(idObj.toString());
            }
        }

        ObjectNode output = objectMapper.createObjectNode();

        ArrayNode idArray = objectMapper.valueToTree(uniqueIds);
        ArrayNode emailArray = objectMapper.valueToTree(uniqueEmails);

        output.set("allRmIds", idArray);
        output.set("allRmEmails", emailArray);

        return output;
    }


    // Method to use only for the emails
    public List<String> getEmails(String exbMemberUserId) {

        if (exbMemberUserId == null) {
            return Collections.emptyList();
        }


        return dataObjectRuntimeService.createDataObjectInstanceQuery()
                .definitionKey(EXB_MEMBER_RELATIONSHIP_KEY)
                .tenantId(TENANT_ID)
                .value("EXB_MEMBER_USER_ID_", exbMemberUserId)
                .operation("findByExbMemberUserId")
                .list()
                .stream()

                .map(DataObjectInstanceVariableContainer::getData)

                .filter(data -> {
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    Object rowExbId = dataMap.get("EXB_MEMBER_USERID_");
                    return exbMemberUserId.equals(asString(rowExbId));
                })

                .map(filteredData -> ((Map<String, Object>) filteredData).get("RM_EMAIL_"))

                .filter(Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .collect(Collectors.toList());


    }

    // --- 3. Method for Aggregated Strings (Comma-Separated Strings) ---

    public ObjectNode getAggregatedRmCString(String exbMemberUserId) {

        Set<String> uniqueIds = new HashSet<>();
        Set<String> uniqueEmails = new HashSet<>();
        collectAllOptions(exbMemberUserId, uniqueIds, uniqueEmails);

        String idString = String.join(",", uniqueIds);
        String emailString = String.join(",", uniqueEmails);

        ObjectNode output = objectMapper.createObjectNode();

        output.put("allRmIds", idString);
        output.put("allRmEmails", emailString);

        return output;
    }



    // ----------------------------------------------------------
    // Helper Methods
    // ----------------------------------------------------------

    /**
     * Calls the existing service and populates the given Sets with unique IDs and emails
     * from both optionsForDedicated and optionsForDeputies lists.
     */
    private void collectAllOptions(String exbMemberUserId, Set<String> uniqueIds, Set<String> uniqueEmails) {
        ObjectNode complexData = dedicatedRMService.fetchByDedicated(exbMemberUserId, null);

        aggregateUserOptions(complexData.get("optionsForDedicated"), uniqueIds, uniqueEmails);
        aggregateUserOptions(complexData.get("optionsForDeputies"), uniqueIds, uniqueEmails);
    }

    /**
     * Helper method to iterate through a JSON array of user objects and extract ID and email.
     */
    private void aggregateUserOptions(JsonNode userArray, Set<String> ids, Set<String> emails) {
        if (userArray != null && userArray.isArray()) {
            for (JsonNode userNode : userArray) {
                JsonNode idNode = userNode.get("id");
                JsonNode emailNode = userNode.get("email");

                if (idNode != null && idNode.isTextual()) {
                    ids.add(idNode.asText());
                }
                if (emailNode != null && emailNode.isTextual()) {
                    emails.add(emailNode.asText());
                }
            }
        }
    }

    private Stream<JsonNode> jsonArrayToStream(JsonNode node) {
        if (node != null && node.isArray()) {
            // Use StreamSupport to create a stream from the array iterator
            return StreamSupport.stream(node.spliterator(), false);
        }
        return Stream.empty(); // Return an empty stream if not a valid array
    }
}
