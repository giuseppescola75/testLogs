package com.juliusbaer.cmt.pat.indexing;

import java.util.Date;

import com.flowable.platform.service.caze.CaseInstanceSearchRepresentation;

public class ComplianceDashboardCaseInstanceRepresentation extends CaseInstanceSearchRepresentation {

    private final String id;
    private final String name;
    private final String caseType;
    private final String caseSubType;
    private final String ownerName;
    private final String businessKey;
    private final Date started;
    private final String businessStatusLabel;
    private final String pcrManuallySelected;
    private final String pcrAction;
    private final String pcrQuantity;
    private final String pcrFiName;
    private final String pcrValor;
    private final String pcrIsin;
    private final String pcrAllConsentDone;
    private final String pcrRequestLimitOrder;
    private final Date pcrDecisionLineManagerDate;
    private final String pcrDecisonLineManager;
    private final Date pcrComplianceDecisionDate;
    private final String pcrDecisionCompliance;
    private final String pcrAdditionalRules;
    private final String pcrHoldingPeriods;
    private final String pcrFpa;
    private final String pcrAdhocConfarea;
    private final String pcrMnpiLimitOrders;
    private final String pcrMnpiExposure;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCaseType() { return caseType; }
    public String getCaseSubType() { return caseSubType; }
    public String getOwnerName() { return ownerName; }
    public String getBusinessKey() { return businessKey; }
    public Date getStarted() { return started; }
    public String getBusinessStatusLabel() { return businessStatusLabel; }
    public String getPcrManuallySelected() { return pcrManuallySelected; }
    public String getPcrAction() { return pcrAction; }
    public String getPcrQuantity() { return pcrQuantity; }
    public String getPcrFiName() { return pcrFiName; }
    public String getPcrValor() { return pcrValor; }
    public String getPcrIsin() { return pcrIsin; }
    public String getPcrAllConsentDone() { return pcrAllConsentDone; }
    public String getPcrRequestLimitOrder() { return pcrRequestLimitOrder; }
    public Date getPcrDecisionLineManagerDate() { return pcrDecisionLineManagerDate; }
    public String getPcrDecisonLineManager() { return pcrDecisonLineManager; }
    public Date getPcrComplianceDecisionDate() { return pcrComplianceDecisionDate; }
    public String getPcrDecisionCompliance() { return pcrDecisionCompliance; }

    public String getPcrAdditionalRules() {
        return pcrAdditionalRules;
    }

    public String getPcrHoldingPeriods() {
        return pcrHoldingPeriods;
    }

    public String getPcrFpa() {
        return pcrFpa;
    }

    public String getPcrAdhocConfarea() {
        return pcrAdhocConfarea;
    }

    public String getPcrMnpiLimitOrders() {
        return pcrMnpiLimitOrders;
    }

    public String getPcrMnpiExposure() {
        return pcrMnpiExposure;
    }

    private ComplianceDashboardCaseInstanceRepresentation(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.caseType = builder.caseType;
        this.caseSubType = builder.caseSubType;
        this.ownerName = builder.ownerName;
        this.businessKey = builder.businessKey;
        this.started = builder.started;
        this.businessStatusLabel = builder.businessStatusLabel;
        this.pcrManuallySelected = builder.fiManuallySelected;
        this.pcrAction = builder.action;
        this.pcrQuantity = builder.quantity;
        this.pcrFiName = builder.fiName;
        this.pcrValor = builder.valor;
        this.pcrIsin = builder.isin;
        this.pcrAllConsentDone = builder.allConsentsProvided;
        this.pcrRequestLimitOrder = builder.requestLimitOrder;
        this.pcrDecisionLineManagerDate = builder.lmDecisionDate;
        this.pcrDecisonLineManager = builder.lmDecision;
        this.pcrComplianceDecisionDate = builder.complianceDecisionDate;
        this.pcrDecisionCompliance = builder.complianceDecision;
        this.pcrAdditionalRules = builder.additionalRules;
        this.pcrHoldingPeriods = builder.holdingPeriods;
        this.pcrFpa = builder.fpa;
        this.pcrAdhocConfarea = builder.adhocConfarea;
        this.pcrMnpiLimitOrders = builder.mnpiLimitOrders;
        this.pcrMnpiExposure = builder.mnpiExposure;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String caseType;
        private String caseSubType;
        private String ownerName;
        private String businessKey;
        private Date started;
        private String businessStatusLabel;
        private String fiManuallySelected;
        private String action;
        private String quantity;
        private String fiName;
        private String valor;
        private String isin;
        private String allConsentsProvided;
        private String requestLimitOrder;
        private Date lmDecisionDate;
        private String lmDecision;
        private Date complianceDecisionDate;
        private String complianceDecision;
        private String additionalRules;
        private String holdingPeriods;
        private String fpa;
        private String adhocConfarea;
        private String mnpiLimitOrders;
        private String mnpiExposure;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder caseType(String caseType) { this.caseType = caseType; return this; }
        public Builder caseSubType(String caseSubType) { this.caseSubType = caseSubType; return this; }
        public Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        public Builder businessKey(String businessKey) { this.businessKey = businessKey; return this; }
        public Builder started(Date started) { this.started = started; return this; }
        public Builder businessStatusLabel(String businessStatusLabel) { this.businessStatusLabel = businessStatusLabel; return this; }
        public Builder fiManuallySelected(String fiManuallySelected) { this.fiManuallySelected = fiManuallySelected; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder quantity(String quantity) { this.quantity = quantity; return this; }
        public Builder fiName(String fiName) { this.fiName = fiName; return this; }
        public Builder valor(String valor) { this.valor = valor; return this; }
        public Builder isin(String isin) { this.isin = isin; return this; }
        public Builder allConsentsProvided(String allConsentsProvided) { this.allConsentsProvided = allConsentsProvided; return this; }
        public Builder requestLimitOrder(String requestLimitOrder) { this.requestLimitOrder = requestLimitOrder; return this; }
        public Builder lmDecisionDate(Date lmDecisionDate) { this.lmDecisionDate = lmDecisionDate; return this; }
        public Builder lmDecision(String lmDecision) { this.lmDecision = lmDecision; return this; }
        public Builder complianceDecisionDate(Date complianceDecisionDate) { this.complianceDecisionDate = complianceDecisionDate; return this; }
        public Builder complianceDecision(String complianceDecision) { this.complianceDecision = complianceDecision; return this; }
        public Builder additionalRules(String additionalRules) { this.additionalRules = additionalRules; return this; }
        public Builder holdingPeriods(String holdingPeriods) { this.holdingPeriods = holdingPeriods; return this; }
        public Builder fpa(String fpa) { this.fpa = fpa; return this; }
        public Builder adhocConfarea(String adhocConfarea) { this.adhocConfarea = adhocConfarea; return this; }
        public Builder mnpiLimitOrders(String mnpiLimitOrders) { this.mnpiLimitOrders = mnpiLimitOrders; return this; }
        public Builder mnpiExposure(String mnpiExposure) { this.mnpiExposure = mnpiExposure; return this; }

        public ComplianceDashboardCaseInstanceRepresentation build() {
            return new ComplianceDashboardCaseInstanceRepresentation(this);
        }
    }
}
