package com.juliusbaer.cmt.pat.dedicatedRM.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.core.idm.api.PlatformIdentityService;
import com.flowable.core.idm.api.PlatformUser;
import com.flowable.dataobject.api.repository.DataObjectDefinition;
import com.flowable.dataobject.api.repository.DataObjectRepositoryService;
import com.flowable.dataobject.api.runtime.DataObjectInstanceVariableContainer;
import com.flowable.dataobject.api.runtime.DataObjectRuntimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("dedicatedRmService")
@RequiredArgsConstructor
public class DedicatedRMService {

    private final PlatformIdentityService platformIdentityService;
    private final DataObjectRuntimeService dataObjectRuntimeService;
    private final DataObjectRepositoryService dataObjectRepositoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TENANT_ID = "bjb-global";
    private static final String RM_GROUP  = "GA_PCL-PCR-RelationshipManager";
    private static final String DEDICATED_RM_KEY = "PCR_DO002";
    private static final String OP_FIND_BY_EXB = "findByExbMemberUserId";
    private static final String PARAM_EXB_MEMBER_USER_ID = "EXB_MEMBER_USER_ID_";



    // ----------------------------------------------------------
    // Public API
    // ----------------------------------------------------------


    public ObjectNode fetchByDedicated(String exbMemberUserId, Boolean dedicated) {
        ObjectNode out = objectMapper.createObjectNode().put("exbMemberUserId", exbMemberUserId);

        List<Map<String, Object>> rows = fetchRows(exbMemberUserId);

        List<ObjectNode> allNodes = new ArrayList<>();
        for (Map<String, Object> m : rows) {
            ObjectNode n = buildUserNodeFromRow(m);
            if (n != null) allNodes.add(n);
        }

        ObjectNode dedicatedNode = allNodes.stream()
                .filter(n -> n.get("isDedicated").asBoolean(false))
                .findFirst()
                .orElse(null);

        List<ObjectNode> deputyNodes = allNodes.stream()
                .filter(n -> n.get("isDeputy").asBoolean(false))
                .collect(Collectors.toList());

        Set<String> dedicatedIdSet = new HashSet<>();
        if (dedicatedNode != null && dedicatedNode.hasNonNull("rmUserId")) {
            dedicatedIdSet.add(dedicatedNode.get("rmUserId").asText());
        }

        Set<String> deputyIdSet = deputyNodes.stream()
                .map(n -> n.get("rmUserId").asText())
                .collect(Collectors.toSet());

        JsonNode slimDedicatedNode = toSlimUserNode(dedicatedNode);

        ArrayNode slimDeputiesArr = objectMapper.createArrayNode();
        for (ObjectNode deputy : deputyNodes) {
            slimDeputiesArr.add(toSlimUserNode(deputy));
        }

        if (Boolean.TRUE.equals(dedicated)) {
            if (slimDedicatedNode != null && slimDedicatedNode.isObject()) {
                out.setAll((ObjectNode) slimDedicatedNode);
            }
            out.set("deputies", objectMapper.createArrayNode());
            out.set("options", allRmOptions(objectMapper, dedicatedIdSet));
            return out;
        }
        if (Boolean.FALSE.equals(dedicated)) {
            out.putNull("dedicated");
            out.set("deputies", slimDeputiesArr);
            out.set("options", allRmOptions(objectMapper, deputyIdSet));
            return out;
        }

        if (slimDedicatedNode != null && slimDedicatedNode.isObject()) {
            out.setAll((ObjectNode) slimDedicatedNode);
        }

        out.set("deputies", slimDeputiesArr);

        out.set("optionsForDedicated", allRmOptions(objectMapper, deputyIdSet));
        out.set("optionsForDeputies",  allRmOptions(objectMapper, dedicatedIdSet));
        return out;
    }


    public ObjectNode getCurrentDeputies(String exbMemberUserId) {
        ObjectNode out = objectMapper.createObjectNode().put("exbMemberUserId", exbMemberUserId);

        List<Map<String, Object>> rows = fetchRows(exbMemberUserId);
        List<ObjectNode> allNodes = new ArrayList<>();
        for (Map<String, Object> m : rows) {
            ObjectNode n = buildUserNodeFromRow(m);
            if (n != null) allNodes.add(n);
        }

        ObjectNode dedicatedNode = allNodes.stream()
                .filter(n -> n.get("isDedicated").asBoolean(false))
                .findFirst()
                .orElse(null);

        List<ObjectNode> deputyNodes = allNodes.stream()
                .filter(n -> n.get("isDeputy").asBoolean(false))
                .collect(Collectors.toList());

        Set<String> deputyIdSet = deputyNodes.stream()
                .map(n -> n.get("rmUserId").asText())
                .collect(Collectors.toSet());


        out.set("dedicated", toSlimUserNode(dedicatedNode));


        ArrayNode slimDeputiesArr = objectMapper.createArrayNode();
        for (ObjectNode deputy : deputyNodes) {
            slimDeputiesArr.add(toSlimUserNode(deputy));
        }
        out.set("deputies", slimDeputiesArr);

         out.set("options", allRmOptions(objectMapper, deputyIdSet));

        return out;
    }



    public JsonNode findDataObjectIdsByExb(String exbMemberUserId) {
        DataObjectDefinition def = getDedicatedRMDefinition();
        // Assuming objectMapper is a class member

        List<DataObjectInstanceVariableContainer> rows = dataObjectRuntimeService
                .createDataObjectInstanceQuery()
                .definitionId(def.getId())
                .tenantId(TENANT_ID)
                .operation(OP_FIND_BY_EXB) // This might still return incorrect rows
                .value(PARAM_EXB_MEMBER_USER_ID, exbMemberUserId)
                .list();

        List<String> idList = rows.stream()
                .filter(row -> { // Added filter
                    Object data = row.getData();
                    if (data instanceof Map<?, ?> m) {
                        // Check: Does the EXB_MEMBER_USERID_ in this row's data
                        // MATCH the exbMemberUserId passed into the method?
                        Object rowExbMemberId = m.get("EXB_MEMBER_USERID_");
                        return exbMemberUserId.equals(asString(rowExbMemberId));
                    }
                    return false; // Discard if not a map
                })
                // This correctly gets the ID_ (database primary key) of the filtered rows
                .map(row -> String.valueOf(row.getId()))
                .collect(Collectors.toList());

        // Format the output: [{"id": "..."}] or []
        List<Map<String, String>> idObjectList = idList.stream()
                .map(id -> {
                    Map<String, String> idMap = new HashMap<>();
                    idMap.put("id", id);
                    return idMap;
                })
                .collect(Collectors.toList());

        return objectMapper.valueToTree(idObjectList);
    }


    public JsonNode findDedicatedDataObjectIdForDeputies(String exbMemberUserId) {
        DataObjectDefinition def = getDedicatedRMDefinition();

        List<DataObjectInstanceVariableContainer> rows = dataObjectRuntimeService
                .createDataObjectInstanceQuery()
                .definitionId(def.getId())
                .tenantId(TENANT_ID)
                .operation(OP_FIND_BY_EXB)
                .value(PARAM_EXB_MEMBER_USER_ID, exbMemberUserId)
                .list();

        List<String> idList = rows.stream()
                .filter(row -> {
                    Object data = row.getData();
                    if (data instanceof Map<?, ?> m) {
                        Object dedicatedValue = m.get("DEDICATED_RM_");
                        boolean isDeputyFlag = isDeputy(dedicatedValue);

                        Object rowExbMemberId = m.get("EXB_MEMBER_USERID_");

                         boolean matchesUser = exbMemberUserId.equals(asString(rowExbMemberId));

                        // 3. Return true only if BOTH conditions are met
                        return isDeputyFlag && matchesUser;
                    }
                    return false;
                })
                .map(row -> String.valueOf(row.getId())) // Prende l'ID solo delle righe filtrate
                .collect(Collectors.toList());

        List<Map<String, String>> idObjectList = idList.stream()
                .map(id -> {
                    Map<String, String> idMap = new HashMap<>();
                    idMap.put("id", id);
                    return idMap;
                })
                .collect(Collectors.toList());

        return objectMapper.valueToTree(idObjectList);
    }

    public JsonNode findDedicatedDataObjectIdForDeicatedRM(String exbMemberUserId) {
        DataObjectDefinition def = getDedicatedRMDefinition();

        List<DataObjectInstanceVariableContainer> rows = dataObjectRuntimeService
                .createDataObjectInstanceQuery()
                .definitionId(def.getId())
                .tenantId(TENANT_ID)
                .operation(OP_FIND_BY_EXB) // This operation might still return wrong rows initially
                .value(PARAM_EXB_MEMBER_USER_ID, exbMemberUserId)
                .list(); // Let's assume this returns a list (maybe incorrect)

        List<String> idList = rows.stream()
                .filter(row -> { // Combined filter
                    Object data = row.getData();
                    if (data instanceof Map<?, ?> m) {
                        Object dedicatedValue = m.get("DEDICATED_RM_");
                        boolean isDedicatedFlag = isDedicated(dedicatedValue);

                           Object rowExbMemberId = m.get("EXB_MEMBER_USERID_");
                        boolean matchesUser = exbMemberUserId.equals(asString(rowExbMemberId));

                        return isDedicatedFlag && matchesUser;
                    }
                    return false;
                })
                .map(row -> String.valueOf(row.getId()))
                .collect(Collectors.toList());

        List<Map<String, String>> idObjectList = idList.stream()
                .map(id -> {
                    Map<String, String> idMap = new HashMap<>();
                    idMap.put("id", id);
                    return idMap;
                })
                .collect(Collectors.toList());

        return objectMapper.valueToTree(idObjectList); // Should  return [] or the ID of the ExbMemebr.
    }



    // ----------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------

    private JsonNode toSlimUserNode(ObjectNode internalNode) {
        if (internalNode == null || internalNode.isNull()) {
            return objectMapper.nullNode();
        }

        ObjectNode slimNode = objectMapper.createObjectNode();
        slimNode.set("id", internalNode.get("rmUserId")); // Mappa rmUserId -> id
        slimNode.set("name", internalNode.get("displayName")); // Aggiunge il campo "name"
        slimNode.set("displayName", internalNode.get("displayName"));
        slimNode.set("email", internalNode.get("email"));

        return slimNode;
    }

    private List<Map<String, Object>> fetchRows(String exbMemberUserId) {
        DataObjectDefinition def = getDedicatedRMDefinition();

        List<DataObjectInstanceVariableContainer> instances = dataObjectRuntimeService
                .createDataObjectInstanceQuery()
                .definitionId(def.getId())
                .tenantId(TENANT_ID)
                .operation(OP_FIND_BY_EXB)
                .value(PARAM_EXB_MEMBER_USER_ID, exbMemberUserId) // This is fine, it calls the (broken) operation
                .list();

        List<Map<String, Object>> out = new ArrayList<>();

        for (var inst : instances) {
            Object d = inst.getData();
            if (d instanceof Map<?,?> m) {

                String rowUserId = asString(m.get("EXB_MEMBER_USERID_"));

                if (exbMemberUserId.equals(rowUserId)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> copy = (Map<String, Object>) m;
                    out.add(copy);
                }
            }
        }

        return out;
    }


    private ObjectNode buildUserNodeFromRow(Map<String, Object> m) {
        String rmUserId = asString(m.get("RM_USER_ID_"));
        if (rmUserId == null || rmUserId.isBlank()) return null;

        ObjectNode n = objectMapper.createObjectNode();
        n.put("rmUserId", rmUserId);

        Object dedicatedValue = m.get("DEDICATED_RM_");
        if (dedicatedValue == null) {
            dedicatedValue = m.get("dedicatedRm"); // Fallback
        }
        Integer dedicatedFlag = to01(dedicatedValue);

        if (dedicatedFlag == null) {
            n.putNull("dedicatedRm");
            n.putNull("role");
            n.put("isDedicated", false);
            n.put("isDeputy", false);
        } else {
            n.put("dedicatedRm", dedicatedFlag);
            n.put("role", roleOf(dedicatedFlag));
            n.put("isDedicated", isDedicated(dedicatedFlag));
            n.put("isDeputy", isDeputy(dedicatedFlag));
        }

        String rowDisplay = asString(m.get("RM_DISPLAY_NAME_"));
        String rowEmail   = asString(m.get("RM_EMAIL_"));

        if (rowDisplay == null || rowDisplay.isBlank() || rowEmail == null) {
            PlatformUser u = findRmUser(rmUserId); // This is now safe
            if (u != null) {
                if (rowDisplay == null || rowDisplay.isBlank()) rowDisplay = u.getDisplayName();
                if (rowEmail   == null)   rowEmail   = u.getEmail();
            }
        }

        n.put("displayName", rowDisplay);
        n.put("email", rowEmail);
        return n;
    }

    /**
     * This helper now correctly excludes a set of IDs.
     */
    private ArrayNode buildOptions(List<PlatformUser> rmUsers, Set<String> excludeIds, ObjectMapper mapper) {
        ArrayNode options = mapper.createArrayNode();
        for (PlatformUser rm : rmUsers) {
            if (excludeIds != null && excludeIds.contains(rm.getId())) {
                continue; // Skip user if they are in the exclusion set
            }
            ObjectNode opt = mapper.createObjectNode();
            opt.put("id", rm.getId());
            String display = rm.getDisplayName() == null ? "" : rm.getDisplayName();
            opt.put("name", display);
            opt.put("displayName", display);
            opt.put("email", rm.getEmail());
            options.add(opt);
        }
        return options;
    }

    private ArrayNode allRmOptions(ObjectMapper mapper, Set<String> exclude) {
        List<PlatformUser> all = platformIdentityService
                .createPlatformUserQuery()
                .memberOfGroupKey(RM_GROUP)
                .list();
        return buildOptions(all, exclude, mapper);
    }

    private DataObjectDefinition getDedicatedRMDefinition() {
        DataObjectDefinition definition = dataObjectRepositoryService
                .createDataObjectDefinitionQuery()
                .tenantId(TENANT_ID)
                .key(DEDICATED_RM_KEY)
                .latestVersion()
                .singleResult();
        if (definition == null) {
            throw new IllegalStateException(
                    "DataObject definition '" + DEDICATED_RM_KEY + "' not found for tenant " + TENANT_ID
            );
        }
        return definition;
    }

    // ----------------------------------------------------------
    // Tiny utils
    // ----------------------------------------------------------


    private PlatformUser findRmUser(String userId) {
        if (userId == null || userId.isBlank()) return null;
        try {
            return platformIdentityService
                    .createPlatformUserQuery()
                    .memberOfGroupKey(RM_GROUP)
                    .userId(userId)
                    .singleResult();
        } catch (Exception e) {
            return null; // Return null and let the code continue
        }
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static Integer to01(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Number n)  return n.intValue() == 0 ? 0 : 1;
        if (raw instanceof Boolean b) return b ? 1 : 0;
        String s = String.valueOf(raw).trim();
        if ("1".equals(s) || "true".equalsIgnoreCase(s))  return 1;
        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return 0;
        return null;
    }

    private static boolean isDedicated(Object v) {
        Integer x = to01(v);
        return x != null && x == 1;
    }

    private static boolean isDeputy(Object v) {
        Integer x = to01(v);
        return x != null && x == 0;
    }

    private static String roleOf(Object v) {
        if (isDedicated(v)) return "DEDICATED_RM";
        if (isDeputy(v))    return "DEPUTY";
        return null;
    }

    public JsonNode filterExbMembersByRmId(Object exbMembersListRaw, String currentRmId) {

        final String finalCurrentRmId = currentRmId.trim();

        List<?> rawList;
        if (exbMembersListRaw instanceof List) {
            rawList = (List<?>) exbMembersListRaw;
        } else {
            rawList = Collections.emptyList();
        }

        List<Map<String, String>> filteredList = rawList.stream()
                .filter(Objects::nonNull)
                .filter(item -> item instanceof DataObjectInstanceVariableContainer)
                .map(item -> (DataObjectInstanceVariableContainer) item)
                .map(container -> {
                    Object rawData = container.getData();
                    if (rawData instanceof Map) {
                        return (Map<String, Object>) rawData;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(dataMap -> {
                    Object dedicatedRmIdObj = dataMap.get("RM_USER_ID_");
                    if (dedicatedRmIdObj != null) {
                        return finalCurrentRmId.equals(dedicatedRmIdObj.toString());
                    }
                    return false;
                })
                .map(filteredDataMap -> {
                    Object idValue = filteredDataMap.get("EXB_MEMBER_USERID_");
                    Object nameValue = filteredDataMap.get("EXB_MEMBER_DISPLAY_NAME_");

                    if (idValue != null && nameValue != null) {
                        String idStr = idValue.toString().trim();
                        String nameStr = nameValue.toString().trim();
                        if (!idStr.isEmpty() && !nameStr.isEmpty()) {
                            Map<String, String> memberInfo = new HashMap<>();
                            memberInfo.put("uid", idStr);
                            memberInfo.put("name", nameStr);
                            return memberInfo;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.valueToTree(filteredList);
        } catch (Exception e) {
            return new ObjectMapper().createArrayNode();
        }
    }


}