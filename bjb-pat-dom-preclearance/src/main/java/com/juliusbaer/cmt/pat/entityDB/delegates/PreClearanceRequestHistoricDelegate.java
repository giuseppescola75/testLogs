package com.juliusbaer.cmt.pat.entityDB.delegates;

import com.juliusbaer.cmt.pat.entityDB.dto.PreClearanceRequestHistoricDto;
import com.juliusbaer.cmt.pat.entityDB.service.PreClearanceRequestHistoricService;
import com.juliusbaer.cmt.pat.entityDB.utils.PreClearanceRequestData;
import com.juliusbaer.cmt.pat.entityDB.utils.PreClearanceRequestExtractor;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("preClearanceRequestHistoricDelegate")
@RequiredArgsConstructor
public class PreClearanceRequestHistoricDelegate implements JavaDelegate {

    private final PreClearanceRequestHistoricService service;
    private final PreClearanceRequestExtractor extractor;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        PreClearanceRequestData data = extractor.extract(delegateExecution);

        PreClearanceRequestHistoricDto historicDto = PreClearanceRequestHistoricDto.builder()
                .caseId(data.getCaseId())
                .createdOn(data.getCreatedOn())
                .historyEntryCreatedOn(LocalDateTime.now())
                .requester(data.getRequester())
                .requestType(data.getRequestType())
                .employeeType(data.getEmployeeType())
                .status(data.getStatus())
                .action(data.getAction())
                .quantity(data.getQuantity())
                .fiName(data.getFiName())
                .valor(data.getValor())
                .isin(data.getIsin())
                .fiManuallySelected(data.isFiManuallySelected())
                .consentAdditionalRules(data.isConsentAdditionalRules())
                .consentHoldingPeriods(data.isConsentHoldingPeriods())
                .consentAdHocConfArea(data.isConsentAdHocConfArea())
                .consentFPA(data.isConsentFPA())
                .consentMNPIExposure(data.isConsentMNPIExposure())
                .consentMNPILimitOrders(data.isConsentMNPILimitOrders())
                .allConsentDone(data.isAllConsentDone())
                .requestLimitOrder(data.isRequestLimitOrder())
                .lmDecisionDate(data.getLmDecisionDate())
                .lmDecision(data.getLmDecision())
                .complianceDecisionDate(data.getComplianceDecisionDate())
                .complianceDecision(data.getComplianceDecision())
                .build();

        service.append(historicDto);
    }
}
