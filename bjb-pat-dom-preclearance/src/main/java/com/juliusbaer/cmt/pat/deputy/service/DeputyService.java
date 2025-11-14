package com.juliusbaer.cmt.pat.deputy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.dataobject.api.repository.DataObjectDefinition;
import com.flowable.dataobject.api.repository.DataObjectRepositoryService;
import com.flowable.dataobject.api.runtime.DataObjectInstanceVariableContainer;
import com.flowable.dataobject.api.runtime.DataObjectRuntimeService;
import com.juliusbaer.gcmt.commons.jid.JidService;
import com.juliusbaer.gcmt.commons.jid.dto.JidUnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("deputyService")
@RequiredArgsConstructor
public class DeputyService {

    private final JidService jidService;
    private final DataObjectRuntimeService dataObjectRuntimeService;
    private final DataObjectRepositoryService dataObjectRepositoryService;

    private static final String TENANT_ID = "bjb-global";
    private static final String DEPUTIES_KEY = "PCR_DO001";

    public ArrayNode getDeputiesFilteredForDropdown(String userId) {
        DataObjectDefinition deputiesDefinition = getDeputiesDefinition();

        Set<String> existingDeputyIds = getExistingDeputyIds(deputiesDefinition);

        JidUnitDto unitDto = jidService.getUnitByUserId(userId);

        /*List<String> filteredMembers = unitDto.getMembers().stream()
                .filter(member -> !member.equals(unitDto.getHead()))
                .filter(member -> !existingDeputyIds.contains(member))
                .toList();*/

        List<String> filteredMembers = unitDto.getMembers().stream()
                .map(this::unifyUserId)
                .filter(member -> !member.equals(unifyUserId(unitDto.getHead())))
                .filter(member -> !existingDeputyIds.contains(member))
                .toList();


        ObjectMapper mapper = new ObjectMapper();
        ArrayNode node = mapper.createArrayNode();

        String unitId = unitDto.getId();
        String head = unifyUserId(unitDto.getHead());
        String deputy = unifyUserId(unitDto.getDeputy());

        for (String member : filteredMembers) {
            String deputyDisplayName = jidService.getUserById(member).getDisplayName();
            String lineManagerDisplayName = jidService.getUserById(head).getDisplayName();
            String email = jidService.getUserById(member).getEmail();

            DeputyResponse response = new DeputyResponse(member, deputyDisplayName, lineManagerDisplayName, unitId, head, deputy, email);
            node.add(jidService.convertToJacksonNode(response, ObjectNode.class));
        }

        return node;
    }


    public ObjectNode preFetchDeputy(String userId) {
        JidUnitDto unitDto = jidService.getUnitByUserId(userId);
        String unitId = unitDto.getId();
        String head = unitDto.getHead();
        String deputy = unitDto.getDeputy();

        ObjectMapper mapper = new ObjectMapper();

        if (deputy == null || !unitDto.getMembers().contains(deputy)) {
            return mapper.createObjectNode();
        }

        String displayNameDeputy = jidService.getUserById(deputy).getDisplayName();
        String displayNameHead = jidService.getUserById(head).getDisplayName();
        String email = jidService.getUserById(deputy).getEmail();

        DeputyResponse response = new DeputyResponse(this.unifyUserId(deputy), displayNameDeputy, displayNameHead, unitId, this.unifyUserId(head), this.unifyUserId(deputy), email);

        return jidService.convertToJacksonNode(response, ObjectNode.class);
    }

    public List<String> getEmailsFromDeputies(String userId) {
        List<DataObjectInstanceVariableContainer> instances = dataObjectRuntimeService.createDataObjectInstanceQuery()
                .definitionKey(DEPUTIES_KEY)
                .tenantId(TENANT_ID)
                .value("lineManagerId", userId)
                .operation("findByUserId")
                .list();

        if (instances == null) {
            return Collections.emptyList();
        }

        return instances.stream()
                .map(DataObjectInstanceVariableContainer::getData)
                .filter(Objects::nonNull)
                .map(data -> ((Map<String, Object>) data).get("email"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private Set<String> getExistingDeputyIds(DataObjectDefinition deputiesDefinition) {
        List<DataObjectInstanceVariableContainer> existingInstances = dataObjectRuntimeService
                .createDataObjectInstanceQuery()
                .definitionId(deputiesDefinition.getId())
                .tenantId(TENANT_ID)
                .operation("findByUserId")
                .list();

        return existingInstances.stream()
                .map(DataObjectInstanceVariableContainer::getData)
                .filter(Map.class::isInstance)
                .map(data -> (Map<String, Object>) data)
                .map(data -> (String) data.get("deputyId"))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    private DataObjectDefinition getDeputiesDefinition() {
        DataObjectDefinition definition = dataObjectRepositoryService
                .createDataObjectDefinitionQuery()
                .tenantId(TENANT_ID)
                .key(DEPUTIES_KEY)
                .latestVersion()
                .singleResult();

        if (definition == null) {
            throw new IllegalStateException("DataObject definition '" + DEPUTIES_KEY + "' not found for tenant " + TENANT_ID);
        }

        return definition;
    }

    public record DeputyResponse(
            String id,
            String displayNameDeputy,
            String displayNameLineManager,
            String unitId,
            String lineManagerId,
            String deputyId,
            String email
    ) {}

    private String unifyUserId(String userId) {
        if (userId != null && !userId.contains("@juliusbaer.com")) {
            userId = userId.concat("@juliusbaer.com");
        }

        return userId;
    }
}
