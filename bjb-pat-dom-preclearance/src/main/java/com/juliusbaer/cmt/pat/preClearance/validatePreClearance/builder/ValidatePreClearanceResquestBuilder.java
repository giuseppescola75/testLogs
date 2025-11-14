package com.juliusbaer.cmt.pat.preClearance.validatePreClearance.builder;

import com.juliusbaer.cmt.pat.app.activoin.openapi.model.FinancialInstrument;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.Underlying;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.ValidatePreClearanceRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * Builder for ValidatePreClearanceRequest.
 * Note: Class name kept as ValidatePreClearanceResquestBuilder to match existing filename.
 */
public class ValidatePreClearanceResquestBuilder {
    private String validationRequestId;
    private String language;
    private String requestedBy;
    private String requestedFor;
    private String zr;
    private Boolean thirdPartyPortfolio;
    private FinancialInstrument financialInstrument;
    private List<Underlying> underlyings;
    private ValidatePreClearanceRequest.TransactionTypeEnum transactionType;
    private Boolean limitOrder;
    private Integer volumeSize;
    private BigDecimal limitPrice;
    private String validityDateFrom;
    private String validityDateTo;

    public static ValidatePreClearanceResquestBuilder builder() {
        return new ValidatePreClearanceResquestBuilder();
    }

    public ValidatePreClearanceResquestBuilder validationRequestId(String validationRequestId) {
        this.validationRequestId = validationRequestId;
        return this;
    }

    public ValidatePreClearanceResquestBuilder language(String language) {
        this.language = language;
        return this;
    }

    public ValidatePreClearanceResquestBuilder requestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
        return this;
    }

    public ValidatePreClearanceResquestBuilder requestedFor(String requestedFor) {
        this.requestedFor = requestedFor;
        return this;
    }

    public ValidatePreClearanceResquestBuilder zr(String zr) {
        this.zr = zr;
        return this;
    }

    public ValidatePreClearanceResquestBuilder thirdPartyPortfolio(Boolean thirdPartyPortfolio) {
        this.thirdPartyPortfolio = thirdPartyPortfolio;
        return this;
    }

    public ValidatePreClearanceResquestBuilder financialInstrument(FinancialInstrument financialInstrument) {
        this.financialInstrument = financialInstrument;
        return this;
    }

    public ValidatePreClearanceResquestBuilder underlyings(List<Underlying> underlyings) {
        this.underlyings = underlyings;
        return this;
    }

    public ValidatePreClearanceResquestBuilder transactionType(ValidatePreClearanceRequest.TransactionTypeEnum transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public ValidatePreClearanceResquestBuilder limitOrder(Boolean limitOrder) {
        this.limitOrder = limitOrder;
        return this;
    }

    public ValidatePreClearanceResquestBuilder volumeSize(Integer volumeSize) {
        this.volumeSize = volumeSize;
        return this;
    }

    public ValidatePreClearanceResquestBuilder limitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
        return this;
    }

    public ValidatePreClearanceResquestBuilder validityDateFrom(String validityDateFrom) {
        this.validityDateFrom = validityDateFrom;
        return this;
    }

    public ValidatePreClearanceResquestBuilder validityDateTo(String validityDateTo) {
        this.validityDateTo = validityDateTo;
        return this;
    }

    public ValidatePreClearanceRequest build() {
        ValidatePreClearanceRequest request = new ValidatePreClearanceRequest();
        request.setValidationRequestId(this.validationRequestId);
        request.setLanguage(this.language);
        request.setRequestedBy(this.requestedBy);
        request.setRequestedFor(this.requestedFor);
        request.setZr(this.zr);
        request.setThirdPartyPortfolio(this.thirdPartyPortfolio);
        request.setFinancialInstrument(this.financialInstrument);
        request.setUnderlyings(this.underlyings);
        request.setTransactionType(this.transactionType);
        request.setLimitOrder(this.limitOrder);
        request.setVolumeSize(this.volumeSize);
        request.setLimitPrice(this.limitPrice);
        request.setValidityDateFrom(this.validityDateFrom);
        request.setValidityDateTo(this.validityDateTo);
        return request;
    }
}
