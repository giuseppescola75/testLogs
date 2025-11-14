package com.juliusbaer.cmt.pat.entityDB.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreClearanceRequestDto {

    private String caseId;

    private Date createdOn;

    private String requester;
    private String requestType;
    private String employeeType;
    private String status;

    private String action;
    private Double quantity;
    private String fiName;
    private String valor;
    private String isin;

    private boolean fiManuallySelected;
    private boolean consentAdditionalRules;
    private boolean consentHoldingPeriods;
    private boolean consentFPA;
    private boolean consentAdHocConfArea;
    private boolean consentMNPILimitOrders;
    private boolean consentMNPIExposure;
    private boolean requestLimitOrder;
    private boolean allConsentDone;

    private LocalDate lmDecisionDate;
    private String lmDecision;
    private LocalDate complianceDecisionDate;
    private String complianceDecision;
}
