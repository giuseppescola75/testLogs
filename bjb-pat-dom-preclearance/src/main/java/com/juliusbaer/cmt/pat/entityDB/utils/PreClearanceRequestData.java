package com.juliusbaer.cmt.pat.entityDB.utils;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class PreClearanceRequestData {
    private String caseId;
    private Date createdOn;
    private String status;
    private String requester;
    private String requestType;
    private String employeeType;
    private String financialInstrumentType;

    private String fiName;
    private String valor;
    private String isin;
    private boolean fiManuallySelected;

    private String action;
    private Double quantity;
    private boolean requestLimitOrder;

    private boolean consentAdditionalRules;
    private boolean consentHoldingPeriods;
    private boolean consentFPA;
    private boolean consentAdHocConfArea;
    private boolean consentMNPILimitOrders;
    private boolean consentMNPIExposure;
    private boolean allConsentDone;

    private String complianceDecision;
    private LocalDate complianceDecisionDate;
    private String lmDecision;
    private LocalDate lmDecisionDate;
}