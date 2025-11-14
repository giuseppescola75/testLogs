package com.juliusbaer.cmt.pat.preClearance.validatePreClearance.builder;

import com.juliusbaer.cmt.pat.app.activoin.openapi.model.FinancialInstrumentIdentifier;
import com.juliusbaer.cmt.pat.app.activoin.openapi.model.Underlying;

public class UnderlyingBuilder {
    private FinancialInstrumentIdentifier identifier;

    public UnderlyingBuilder identifier(FinancialInstrumentIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public Underlying build() {
        Underlying underlying = new Underlying();
        underlying.setIdentifier(this.identifier);
        return underlying;
    }
}

