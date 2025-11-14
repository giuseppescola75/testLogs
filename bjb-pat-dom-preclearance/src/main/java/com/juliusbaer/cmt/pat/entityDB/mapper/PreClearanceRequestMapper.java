package com.juliusbaer.cmt.pat.entityDB.mapper;

import com.juliusbaer.cmt.pat.entityDB.dto.PreClearanceRequestDto;
import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PreClearanceRequestMapper {

    public PreClearanceRequestDto toDto(PreClearanceRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        return PreClearanceRequestDto.builder()
                .caseId(entity.getCaseId())
                .createdOn(entity.getCreatedOn())
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
                .lmDecisionDate(entity.getLmDecisionDate())
                .lmDecision(entity.getLmDecision())
                .complianceDecisionDate(entity.getComplianceDecisionDate())
                .complianceDecision(entity.getComplianceDecision())
                .build();
    }

    public PreClearanceRequestEntity toEntity(PreClearanceRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return PreClearanceRequestEntity.builder()
                .caseId(dto.getCaseId())
                .createdOn(dto.getCreatedOn())
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
                .lmDecisionDate(dto.getLmDecisionDate())
                .lmDecision(dto.getLmDecision())
                .complianceDecisionDate(dto.getComplianceDecisionDate())
                .complianceDecision(dto.getComplianceDecision())
                .build();
    }

    public List<PreClearanceRequestDto> toDtoList(List<PreClearanceRequestEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PreClearanceRequestEntity> toEntityList(List<PreClearanceRequestDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
