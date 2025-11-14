package com.juliusbaer.cmt.pat.identityLinks;

import java.util.List;

import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PcrIdentityLinkService {

    private final TaskService taskService;

    public void addIdentityLinkToTask(String taskId, ArrayNode userIds, String identityLinkType) {
        for (JsonNode userIdNode : userIds) {
            String userId = userIdNode.asText();
            taskService.addUserIdentityLink(taskId, userId, identityLinkType);
        }
    }
}
