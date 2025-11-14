package com.juliusbaer.cmt.pat.entityDB.mapper;

import com.juliusbaer.cmt.pat.entityDB.dto.PreClearanceRequestHistoricDto;
import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestEntity;
import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestHistoricEntity;
import org.springframework.stereotype.Component;

@Component
public class PreClearanceRequestHistoricMapper {

    public PreClearanceRequestHistoricDto toDto(PreClearanceRequestHistoricEntity entity) {
        return PreClearanceRequestHistoricDto.builder()
                .id(entity.getId())
                .caseId(entity.getCurrentRequest() != null ? entity.getCurrentRequest().getCaseId() : null) // ← HIER
                .createdOn(entity.getCreatedOn())
                .historyEntryCreatedOn(entity.getHistoryEntryCreatedOn())
                .requester(entity.getRequester())
                .requestType(entity.getRequestType())
                .employeeType(entity.getEmployeeType())
                .status(entity.getStatus())
                .action(entity.getAction())
                .quantity(entity.getQuantity())
                .fiName(entity.getFiName())
                .valor(entity.getValor())
                .isin(entity.getIsin())
                .fiManuallySelected(entity.isFiManuallySelected())
                .consentAdditionalRules(entity.isConsentAdditionalRules())
                .consentHoldingPeriods(entity.isConsentHoldingPeriods())
                .consentFPA(entity.isConsentFPA())
                .consentAdHocConfArea(entity.isConsentAdHocConfArea())
                .consentMNPILimitOrders(entity.isConsentMNPILimitOrders())
                .consentMNPIExposure(entity.isConsentMNPIExposure())
                .requestLimitOrder(entity.isRequestLimitOrder())
                .allConsentDone(entity.isAllConsentDone())
                .lmDecision(entity.getLmDecision())
                .lmDecisionDate(entity.getLmDecisionDate())
                .complianceDecision(entity.getComplianceDecision())
                .complianceDecisionDate(entity.getComplianceDecisionDate())
                .build();
    }

    public PreClearanceRequestHistoricEntity toEntity(
            PreClearanceRequestHistoricDto dto,
            PreClearanceRequestEntity currentRequest
    ) {
        return PreClearanceRequestHistoricEntity.builder()
                .id(dto.getId())
                .currentRequest(currentRequest)
                .createdOn(dto.getCreatedOn())
                .historyEntryCreatedOn(dto.getHistoryEntryCreatedOn())
                .requester(dto.getRequester())
                .requestType(dto.getRequestType())
                .employeeType(dto.getEmployeeType())
                .status(dto.getStatus())
                .action(dto.getAction())
                .quantity(dto.getQuantity())
                .fiName(dto.getFiName())
                .valor(dto.getValor())
                .isin(dto.getIsin())
                .fiManuallySelected(dto.isFiManuallySelected())
                .consentAdditionalRules(dto.isConsentAdditionalRules())
                .consentHoldingPeriods(dto.isConsentHoldingPeriods())
                .consentFPA(dto.isConsentFPA())
                .consentAdHocConfArea(dto.isConsentAdHocConfArea())
                .consentMNPILimitOrders(dto.isConsentMNPILimitOrders())
                .consentMNPIExposure(dto.isConsentMNPIExposure())
                .requestLimitOrder(dto.isRequestLimitOrder())
                .allConsentDone(dto.isAllConsentDone())
                .lmDecision(dto.getLmDecision())
                .lmDecisionDate(dto.getLmDecisionDate())
                .complianceDecision(dto.getComplianceDecision())
                .complianceDecisionDate(dto.getComplianceDecisionDate())
                .build();
    }
}
