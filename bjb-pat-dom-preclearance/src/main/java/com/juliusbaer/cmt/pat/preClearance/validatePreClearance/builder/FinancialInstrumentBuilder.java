package com.juliusbaer.cmt.pat.preClearance.validatePreClearance.builder;

import com.juliusbaer.cmt.pat.app.activoin.openapi.model.FinancialInstrument;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.FinancialInstrumentIdentifier;

public class FinancialInstrumentBuilder {
    private FinancialInstrumentIdentifier identifier;

    public FinancialInstrumentBuilder identifier(FinancialInstrumentIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public FinancialInstrument build() {
        FinancialInstrument fi = new FinancialInstrument();
        fi.setIdentifier(this.identifier);
        return fi;
    }
}
