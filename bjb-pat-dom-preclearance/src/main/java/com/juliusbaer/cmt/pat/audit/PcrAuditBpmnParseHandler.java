package com.juliusbaer.cmt.pat.audit;

import com.juliusbaer.cmt.pat.logging.LogVocab;
import com.juliusbaer.cmt.pat.logging.Loggers;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.flowable.engine.parse.BpmnParseHandler;
import org.flowable.idm.api.User;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PcrAuditBpmnParseHandler implements BpmnParseHandler {

    private static final Logger BAL = Loggers.BAL;
    private static final Logger ATL = Loggers.ATL;

    @Override
    public Collection<Class<? extends BaseElement>> getHandledTypes() {
        Set<Class<? extends BaseElement>> types = new HashSet<>();
        types.add(UserTask.class);
        return types;
    }

    @Override
    public void parse(BpmnParse bpmnParse, BaseElement baseElement) {
        if (baseElement instanceof UserTask userTask) {

            ATL.info("action={},event={},taskId={},taskName={},processId={},caseId={},userId={}",
                    LogVocab.ActionATL.STARTED,TaskListener.EVENTNAME_CREATE, userTask.getId() ,userTask.getName(),this.getProcessId(bpmnParse), "n/a", userTask.getAssignee());
            FlowableListener auditCreation = new FlowableListener();
            auditCreation.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
            auditCreation.setImplementation("${pcrAuditTaskListener}");
            auditCreation.setEvent(TaskListener.EVENTNAME_CREATE);

            FlowableListener auditCompletion = new FlowableListener();
            auditCompletion.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
            auditCompletion.setImplementation("${pcrAuditTaskListener}");
            auditCompletion.setEvent(TaskListener.EVENTNAME_COMPLETE);

            userTask.getTaskListeners().add(auditCreation);
            userTask.getTaskListeners().add(auditCompletion);
        }
    }

    private String getProcessId(BpmnParse bpmnParse) {
        return bpmnParse.getDeployment().getId();
    }

}

