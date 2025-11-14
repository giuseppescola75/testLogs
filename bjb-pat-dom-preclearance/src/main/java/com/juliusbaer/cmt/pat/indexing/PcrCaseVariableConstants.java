package com.juliusbaer.cmt.pat.indexing;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PcrCaseVariableConstants {

    public static final String FIELD_CASE_SEQUENCE_VALUE = "caseSequenceValue";
    public static final String PCR_CASE_DEFINITION_KEY = "PCR_C001";
    public static final Set<String> CASE_DEFINITION_KEYS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(PCR_CASE_DEFINITION_KEY)));
    public static final String FIELD_OWNER_NAME = "ownerName";
    public static final String FIELD_FI_NAME = "assBjbNameLongIssuer";
    public static final String FIELD_FINANCIAL_INSTRUMENTS = "financialInstruments";
    public static final String FIELD_CASE_BUSINESS_STATE = "caseBusinessState";
    public static final String FIELD_ACTION = "action";
    public static final String FIELD_ORDER_DETAILS = "captureOrderDetails";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_VALOR = "valorNo";
    public static final String FIELD_ISIN = "isin";
    public static final String FIELD_FINANCIAL_INSTRUMENT_TYPE = "financialInstrumentType";
    public static final String FIELD_ALL_CONSENTS = "allConsentDone";
    public static final String FIELD_CONSENT = "consent";
    public static final String FIELD_REQUEST_LIMIT_ORDER = "requestForLimitOrder";
    public static final String FIELD_LM_DECISION_DATE = "";
    public static final String FIELD_LM_DECISION = "decisonLineManager";
    public static final String FIELD_COMPLIANCE_DECISION_DATE = "";
    public static final String FIELD_COMPLIANCE_DECISION = "decisionLocalCompliance";

    public static final String FIELD_ADDITIONAL_RULES = "additionalRules";
    public static final String FIELD_HOLDING_PERIODS = "holdingPeriods";
    public static final String FIELD_FPA = "fPA";
    public static final String FIELD_ADHOC_CONFAREA = "adHocConfArea";
    public static final String FIELD_MNPIL_LIMIT_ORDERS = "mNPILimitOrders";
    public static final String FIELD_MNPI_EXPOSURE = "mNPIExposure";
    public static final String FIELD_PRECLEARANCE_REQUEST = "preClearanceRequest";
    public static final String FIELD_FINANCIAL_INSTRUMENTS_LIST = "financialInstrumentsList";
    public static final String FIELD_FI = "financialInstrument";
    public static final String FIELD_TEAM_ID = "teamId";
    public static final String FIELD_FINANCIAL_INSTRUMENT = "financialInstrument";
    public static final String FIELD_CREATOR_USER_ROLE = "creatorUserRole";
    public static final String FIELD_LOCAL_COMPLIANCE_GROUP = "localComplianceGroup";
}
