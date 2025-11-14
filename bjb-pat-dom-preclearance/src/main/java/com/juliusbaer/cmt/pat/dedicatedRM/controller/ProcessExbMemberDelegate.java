package com.juliusbaer.cmt.pat.dedicatedRM.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowable.dataobject.api.runtime.DataObjectInstanceVariableContainer;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component("processExBMembersDelegate")
public class ProcessExbMemberDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        ObjectMapper objectMapper = new ObjectMapper();

        String originalRmId = (String) execution.getVariable("caseOwnerUNumber");

        if (originalRmId == null || originalRmId.trim().isEmpty()) {
            System.err.println("ERRORE in ProcessExbMemberDelegate: Variabile 'caseOwnerUNumber' non trovata o vuota!");
            execution.setVariable("exbMembersInfo", objectMapper.createArrayNode());
            return;
        }

        String processedRmId = originalRmId.trim();
        if (!processedRmId.contains("@")) {
          processedRmId = processedRmId + "@juliusbaer.com";
        }

         final String finalCurrentRmId = processedRmId;

          Object raw = execution.getVariable("exbMembersList");
        List<?> rawList;
        if (raw instanceof List) {
            rawList = (List<?>) raw;
        } else {
            rawList = Collections.emptyList();
        }

        List<Map<String, String>> membersInfoList = rawList.stream()
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


        JsonNode membersInfoNode = objectMapper.valueToTree(membersInfoList);

        execution.setVariable("exbMembersInfo", membersInfoNode);
    }
}