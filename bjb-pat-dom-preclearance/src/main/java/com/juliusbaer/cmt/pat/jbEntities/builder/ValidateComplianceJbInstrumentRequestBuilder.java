package com.juliusbaer.cmt.pat.jbEntities.builder;

import java.util.List;
import java.util.ArrayList;

import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.FinancialInstrumentIdentifier;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.ValidateComplianceJbInstrumentRequest;


public class ValidateComplianceJbInstrumentRequestBuilder {

    private FinancialInstrumentIdentifier financialInstrument;
    private List<FinancialInstrumentIdentifier> underlyings;

    public ValidateComplianceJbInstrumentRequestBuilder() {
        this.underlyings = new ArrayList<>();
    }


    public ValidateComplianceJbInstrumentRequestBuilder withFinancialInstrument(FinancialInstrumentIdentifier financialInstrument) {
        this.financialInstrument = financialInstrument;
        return this;
    }


    public ValidateComplianceJbInstrumentRequestBuilder withUnderlyings(List<FinancialInstrumentIdentifier> underlyings) {
        this.underlyings = underlyings != null ? new ArrayList<>(underlyings) : new ArrayList<>();
        return this;
    }


    public ValidateComplianceJbInstrumentRequestBuilder addUnderlying(FinancialInstrumentIdentifier underlying) {
        if (this.underlyings == null) {
            this.underlyings = new ArrayList<>();
        }
        this.underlyings.add(underlying);
        return this;
    }


    public ValidateComplianceJbInstrumentRequestBuilder clearUnderlyings() {
        if (this.underlyings != null) {
            this.underlyings.clear();
        }
        return this;
    }


    public ValidateComplianceJbInstrumentRequest build() {
        ValidateComplianceJbInstrumentRequest request = new ValidateComplianceJbInstrumentRequest();
        request.setFinancialInstrument(this.financialInstrument);
        request.setUnderlyings(this.underlyings);
        return request;
    }


    public static ValidateComplianceJbInstrumentRequestBuilder builder() {
        return new ValidateComplianceJbInstrumentRequestBuilder();
    }
}

