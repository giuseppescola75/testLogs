package com.juliusbaer.cmt.pat.identityLinks;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.entitylink.api.EntityLinkInfo;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Service;

import com.juliusbaer.cmt.pat.indexing.PcrCaseVariableConstants;
import com.juliusbaer.cmt.pat.indexing.PcrTaskVariableConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PcrIdentityLinksTaskListener extends AbstractFlowableEventListener {

    private final CmmnRuntimeService cmmnRuntimeService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    @Override
    public void onEvent(FlowableEvent event) {
        if (FlowableEngineEventType.TASK_CREATED.equals(event.getType())) {
            createIdentityLinkFromCaseVariable(event, PcrCaseVariableConstants.FIELD_LOCAL_COMPLIANCE_GROUP, "localComplianceGroup");
        }
    }

    private void createIdentityLinkFromCaseVariable(FlowableEvent event, String variableName, String identityLinkType) {
        FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        TaskEntity task = (TaskEntity) entityEvent.getEntity();

        String caseId = findAssociatedCaseId(task);
        if (caseId != null) {
            String variableValue = fetchCaseVariableValue(caseId, variableName);
            if (variableValue != null) {
                taskService.addGroupIdentityLink(task.getId(), variableValue, identityLinkType);
                task.setVariable(PcrCaseVariableConstants.FIELD_LOCAL_COMPLIANCE_GROUP, variableValue);
            }
        }
    }

    private String findAssociatedCaseId(TaskEntity task) {
        return runtimeService.getEntityLinkParentsForTask(task.getId())
                .stream()
                .filter(link -> "cmmn".equals(link.getScopeType()))
                .findFirst()
                .map(EntityLinkInfo::getScopeId)
                .orElse(null);
    }

    private String fetchCaseVariableValue(String caseId, String variableName) {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(caseId)
                .includeCaseVariables()
                .singleResult();

        if (caseInstance == null || caseInstance.getCaseVariables() == null)
            return null;

        Object variableValue = caseInstance.getCaseVariables().get(variableName);
        return variableValue != null ? variableValue.toString() : null;
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
