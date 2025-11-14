package com.juliusbaer.cmt.pat.audit;

import com.flowable.audit.api.AuditService;
import com.flowable.core.idm.api.PlatformIdentityService;
import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import com.juliusbaer.gcmt.commons.jid.utils.AuditLogEntry;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.entitylink.api.EntityLink;
import org.flowable.entitylink.api.EntityLinkInfo;
import org.flowable.entitylink.api.EntityLinkType;
import org.flowable.entitylink.api.HierarchyType;
import org.flowable.task.service.delegate.BaseTaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.juliusbaer.cmt.pat.audit.PcrAuditService.BJB_GLOBAL;

@Service
public class PcrAuditTaskListener implements TaskListener {
    private static final Logger ATL = Loggers.ATL;
    private static final Logger BAL = Loggers.BAL;

    private final AuditService auditService;
    private final CmmnEngineConfiguration cmmnEngineConfiguration;
    private final PlatformIdentityService platformIdentityService;

    public PcrAuditTaskListener(AuditService auditService, CmmnEngineConfiguration cmmnEngineConfiguration, PlatformIdentityService platformIdentityService) {
        this.auditService = auditService;
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        this.platformIdentityService = platformIdentityService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
       /* ATL.info("action={},status={}", LogVocab.ActionATL.COMPLETED.name(), LogVocab.StatusATL.SUCCESS.name());
        if (delegateTask.getEventName().equals(BaseTaskListener.EVENTNAME_CREATE)) {
            AuditLogEntry auditLogEntry = new AuditLogEntry(AuditLogEntry.Category.SYSTEM, "User Task '" + delegateTask.getName() + getAssignee(delegateTask.getAssignee()));
            auditService.createAuditInstanceBuilder().scopeId(getCaseId(delegateTask.getProcessInstanceId())).scopeType(ScopeTypes.CMMN).type("Task").tenantId(BJB_GLOBAL).payload(auditLogEntry).create();
            ATL.info("action={},event={},taskId={},processId={},userId={}", LogVocab.ActionATL.COMPLETED.name(), "Task created", delegateTask.getId(), delegateTask.getProcessInstanceId(), delegateTask.getAssignee());
            BAL.info("action={},status={},event={}", LogVocab.ActionBAL.UPDATE.name(), LogVocab.StatusBAL.SUCCESS.name(), delegateTask.getEventName());
        }

        if (delegateTask.getEventName().equals(BaseTaskListener.EVENTNAME_COMPLETE)) {
            AuditLogEntry auditLogEntry = new AuditLogEntry(AuditLogEntry.Category.SYSTEM, "User Task '" + delegateTask.getName() + "' completed by " + getDisplayName(delegateTask.getAssignee()));
            auditService.createAuditInstanceBuilder().scopeId(getCaseId(delegateTask.getProcessInstanceId())).scopeType(ScopeTypes.CMMN).type("Task").tenantId(BJB_GLOBAL).payload(auditLogEntry).create();
            ATL.info("action={},event={},taskId={},processId={},userId={}", LogVocab.ActionATL.COMPLETED.name(), "Task completed", delegateTask.getId(), delegateTask.getProcessInstanceId(), delegateTask.getAssignee());
            BAL.info("action={},status={},event={}", LogVocab.ActionBAL.UPDATE.name(), LogVocab.StatusBAL.SUCCESS.name(), delegateTask.getEventName());
        }

        */
    }

    private String getCaseId(String id) {
        Optional<EntityLink> rootEntityLink = cmmnEngineConfiguration.getEntityLinkServiceConfiguration().getEntityLinkService().findEntityLinksByReferenceScopeIdAndType(id, ScopeTypes.BPMN, EntityLinkType.CHILD).stream().filter((entityLink) -> HierarchyType.ROOT.equals(entityLink.getHierarchyType())).findFirst();

        return rootEntityLink.map(EntityLinkInfo::getRootScopeId).orElse(null);
    }

    private String getDisplayName(String userId) {
        if (userId == null) {
            return "";
        }
        return platformIdentityService.createPlatformUserQuery().userId(userId).singleResult().getDisplayName();
    }

    private String getAssignee(String userId) {
        if (userId != null) {
            return " assigned to " + getDisplayName(userId);
        }
        return "";
    }
}
