package com.juliusbaer.cmt.pat.entityDB.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "pre_clearance_request_historic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreClearanceRequestHistoricEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_", nullable = false)
    private Long id;

    @Column(name = "CASE_CREATED_ON_", nullable = false)
    private Date createdOn;

    @Column(name = "HISTORIC_ENTRY_CREATED_ON_", nullable = false)
    private LocalDateTime historyEntryCreatedOn;

    @Column(name = "REQUESTER_", nullable = false)
    private String requester;

    @Column(name = "REQUEST_TYPE_")
    private String requestType;

    @Column(name = "EMPLOYEE_TYPE_")
    private String employeeType;

    @Column(name = "STATUS_", nullable = false)
    private String status;

    @Column(name = "ACTION_")
    private String action;

    @Column(name = "QUANTITY_")
    private Double quantity;

    @Column(name = "FI_NAME_")
    private String fiName;

    @Column(name = "VALOR_")
    private String valor;

    @Column(name = "ISIN_")
    private String isin;

    @Column(name = "FI_MANUALLY_SELECTED_")
    private boolean fiManuallySelected;

    @Column(name = "CONSENT_ADDITIONAL_RULES_")
    private boolean consentAdditionalRules;

    @Column(name = "CONSENT_HOLDING_PERIODS_")
    private boolean consentHoldingPeriods;

    @Column(name = "CONSENT_FPA_")
    private boolean consentFPA;

    @Column(name = "CONSENT_AD_HOC_CONF_AREA_")
    private boolean consentAdHocConfArea;

    @Column(name = "CONSENT_MNPI_LIMIT_ORDERS_")
    private boolean consentMNPILimitOrders;

    @Column(name = "CONSENT_MNPI_EXPOSURE_")
    private boolean consentMNPIExposure;

    @Column(name = "ALL_CONSENT_DONE_")
    private boolean allConsentDone;

    @Column(name = "REQUEST_LIMIT_ORDER_")
    private boolean requestLimitOrder;

    @Column(name = "LM_DECISION_DATE_")
    private LocalDate lmDecisionDate;

    @Column(name = "LM_DECISION_", length = 50)
    private String lmDecision;

    @Column(name = "COMPLIANCE_DECISION_DATE_")
    private LocalDate complianceDecisionDate;

    @Column(name = "COMPLIANCE_DECISION_", length = 50)
    private String complianceDecision;

    @ManyToOne
    @JoinColumn(name = "CASE_ID_", referencedColumnName = "ID_")
    private PreClearanceRequestEntity currentRequest;
}
