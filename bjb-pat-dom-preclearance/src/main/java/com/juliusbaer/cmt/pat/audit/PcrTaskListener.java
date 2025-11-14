package com.juliusbaer.cmt.pat.audit;

import com.flowable.audit.api.AuditService;
import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import com.juliusbaer.gcmt.commons.jid.utils.AuditLogEntry;
import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.entitylink.api.EntityLink;
import org.flowable.entitylink.api.EntityLinkInfo;
import org.flowable.entitylink.api.EntityLinkType;
import org.flowable.entitylink.api.HierarchyType;
import org.flowable.idm.api.User;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.flowable.core.idm.api.PlatformIdentityService;
import com.juliusbaer.cmt.pat.indexing.PcrCaseVariableConstants;
import com.juliusbaer.cmt.pat.indexing.PcrTaskVariableConstants;
import com.juliusbaer.gcmt.commons.jid.JidService;
import com.juliusbaer.gcmt.commons.jid.dto.JidUnitDto;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.juliusbaer.cmt.pat.audit.PcrAuditService.BJB_GLOBAL;

@Service
@RequiredArgsConstructor
public class PcrTaskListener extends AbstractFlowableEventListener {

    private final PlatformIdentityService platformIdentityService;
    private final JidService jidService;
    private final TaskService taskService;
    private final CmmnRuntimeService cmmnRuntimeService;
    private final RuntimeService runtimeService;
    private final AuditService auditService;
    private final CmmnEngineConfiguration cmmnEngineConfiguration;

    private static final Logger BAL = Loggers.BAL;
    private static final Logger ATL = Loggers.ATL;


    private final ConcurrentMap<String, Boolean> createdLogged = new ConcurrentHashMap<>();

    @Override
    public void onEvent(FlowableEvent event) {

        if (FlowableEngineEventType.TASK_CREATED.equals(event.getType())) {
            FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
            TaskEntity task = (TaskEntity) entityEvent.getEntity();

            saveCaseCreatorRoleOnTask(event);

            createdLogged.computeIfAbsent(task.getId(), taskId -> {
                auditTaskCreated(task);
                return true;
            });
        }

        if (FlowableEngineEventType.TASK_ASSIGNED.equals(event.getType())) {
            FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
            TaskEntity task = (TaskEntity) entityEvent.getEntity();
            User assignee = StringUtils.isBlank(task.getAssignee()) ?
                    null :
                    this.platformIdentityService.createUserQuery().userId(task.getAssignee()).singleResult();

            if (assignee != null) {
                JidUnitDto unit = jidService.getUnitByUserId(assignee.getId());
                task.setVariable("teamId", unit != null ? unit.getId() : "");
                taskService.setVariable(task.getId(), "teamId", unit.getId());

                createdLogged.computeIfAbsent(task.getId(), taskId -> {
                    auditTaskCreated(task);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return true;
                });

                auditTaskAssigned(task, assignee);
            }
        }

        if (FlowableEngineEventType.TASK_COMPLETED.equals(event.getType())) {
            FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
            TaskEntity task = (TaskEntity) entityEvent.getEntity();

            auditTaskCompleted(task);
            createdLogged.remove(task.getId());
        }
    }

    private void auditTaskCreated(TaskEntity task) {
        AuditLogEntry auditLogEntry = new AuditLogEntry(
                AuditLogEntry.Category.USER,
                "User Task '" + task.getName() + "' created");
        auditService.createAuditInstanceBuilder()
                .scopeId(getCaseId(task.getProcessInstanceId()))
                .scopeType(ScopeTypes.CMMN)
                .type("Task")
                .tenantId(BJB_GLOBAL)
                .payload(auditLogEntry)
                .create();
        logTaskEvent(task, "created");
    }

    private void auditTaskAssigned(TaskEntity task, User assignee) {
        AuditLogEntry auditLogEntry = new AuditLogEntry(AuditLogEntry.Category.USER, "User Task '" + task.getName() + "' assigned to " + getDisplayName(assignee.getId()));
        auditService.createAuditInstanceBuilder()
                .scopeId(getCaseId(task.getProcessInstanceId()))
                .scopeType(ScopeTypes.CMMN)
                .type("Task")
                .tenantId(BJB_GLOBAL)
                .payload(auditLogEntry)
                .create();
        logTaskEvent(task, "assigned");
    }

    private void auditTaskCompleted(TaskEntity task) {
        String userDisplay = (task.getAssignee() != null) ? getDisplayName(task.getAssignee()) : "User";
        AuditLogEntry auditLogEntry = new AuditLogEntry(
                AuditLogEntry.Category.USER,
                "User Task '" + task.getName() + "' completed by " + userDisplay);
        auditService.createAuditInstanceBuilder()
                .scopeId(getCaseId(task.getProcessInstanceId()))
                .scopeType(ScopeTypes.CMMN)
                .type("Task")
                .tenantId(BJB_GLOBAL)
                .payload(auditLogEntry)
                .create();
        logTaskEvent(task, "completed");
    }

    private void saveCaseCreatorRoleOnTask(FlowableEvent event) {
        FlowableEntityEvent entityEvent = (FlowableEntityEvent) event;
        TaskEntity task = (TaskEntity) entityEvent.getEntity();

        String caseId = findAssociatedCaseId(task);
        if (caseId != null) {
            String caseCreatorRole = fetchCaseCreatorRole(caseId);
            if (caseCreatorRole != null) {
                task.setVariable(PcrTaskVariableConstants.FIELD_CREATOR_USER_ROLE, caseCreatorRole);
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

    private String fetchCaseCreatorRole(String caseId) {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceQuery()
                .caseInstanceId(caseId)
                .includeCaseVariables()
                .singleResult();

        if (caseInstance == null || caseInstance.getCaseVariables() == null)
            return null;

        Object role = caseInstance.getCaseVariables().get(PcrCaseVariableConstants.FIELD_CREATOR_USER_ROLE);
        return role != null ? role.toString() : null;
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    private String getDisplayName(String userId) {
        if (userId == null) {
            return "";
        }
        return platformIdentityService.createPlatformUserQuery().userId(userId).singleResult().getDisplayName();
    }

    private String getCaseId(String id) {
        Optional<EntityLink> rootEntityLink = cmmnEngineConfiguration.getEntityLinkServiceConfiguration().getEntityLinkService().findEntityLinksByReferenceScopeIdAndType(id, ScopeTypes.BPMN, EntityLinkType.CHILD).stream().filter((entityLink) -> HierarchyType.ROOT.equals(entityLink.getHierarchyType())).findFirst();

        return rootEntityLink.map(EntityLinkInfo::getRootScopeId).orElse(null);
    }

    private void logTaskEvent(TaskEntity task, String event) {
        String caseId = getCaseId(task.getProcessInstanceId());

        ATL.info("action={},event={},taskId={},taskName={},processId={},caseId={},userId={}",
                LogVocab.ActionATL.COMPLETED.name(),
                event,
                task.getId(),
                task.getName(),
                task.getProcessInstanceId(),
                caseId,
                task.getAssignee());

        BAL.info("action={},event={},taskId={},taskName={},processId={},caseId={},userId={}",
                LogVocab.ActionBAL.UPDATE.name(),
                LogVocab.StatusBAL.SUCCESS.name(),
                event,
                task.getId(),
                task.getName(),
                task.getProcessInstanceId(),
                caseId,
                task.getAssignee());

    }
}
