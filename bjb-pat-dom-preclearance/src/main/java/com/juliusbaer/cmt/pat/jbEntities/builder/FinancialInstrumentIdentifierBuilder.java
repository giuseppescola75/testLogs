package com.juliusbaer.cmt.pat.jbEntities.builder;


import com.juliusbaer.cmt.pat.app.compliancejb.openapi.model.FinancialInstrumentIdentifier;

/**
 * Builder class for FinancialInstrumentIdentifier
 */
public class FinancialInstrumentIdentifierBuilder {

    private String jbgid;
    private String isin;
    private String valor;
    private String jbvalor;
    private String bbgid;

    public FinancialInstrumentIdentifierBuilder() {
    }

    /**
     * Sets the JB internal unique identifier for the financial instrument
     * @param jbgid the JB global ID
     * @return this builder instance
     */
    public FinancialInstrumentIdentifierBuilder withJbgid(String jbgid) {
        this.jbgid = jbgid;
        return this;
    }

    /**
     * Sets the International Securities Identification Number
     * @param isin the ISIN
     * @return this builder instance
     */
    public FinancialInstrumentIdentifierBuilder withIsin(String isin) {
        this.isin = isin;
        return this;
    }

    /**
     * Sets the Valorennummer (unique identifier for financial instruments in Switzerland)
     * @param valor the valor number
     * @return this builder instance
     */
    public FinancialInstrumentIdentifierBuilder withValor(String valor) {
        this.valor = valor;
        return this;
    }

    /**
     * Sets the JB internal version of Valorennummer
     * @param jbvalor the JB valor number
     * @return this builder instance
     */
    public FinancialInstrumentIdentifierBuilder withJbvalor(String jbvalor) {
        this.jbvalor = jbvalor;
        return this;
    }

    /**
     * Sets the Bloomberg ID
     * @param bbgid the Bloomberg ID
     * @return this builder instance
     */
    public FinancialInstrumentIdentifierBuilder withBbgid(String bbgid) {
        this.bbgid = bbgid;
        return this;
    }

    /**
     * Builds the FinancialInstrumentIdentifier instance
     * @return the constructed FinancialInstrumentIdentifier
     */
    public FinancialInstrumentIdentifier build() {
        FinancialInstrumentIdentifier identifier = new FinancialInstrumentIdentifier();
        identifier.setJbgid(this.jbgid);
        identifier.setIsin(this.isin);
        identifier.setValor(this.valor);
        identifier.setJbvalor(this.jbvalor);
        identifier.setBbgid(this.bbgid);
        return identifier;
    }

    /**
     * Creates a new builder instance
     * @return new builder instance
     */
    public static FinancialInstrumentIdentifierBuilder builder() {
        return new FinancialInstrumentIdentifierBuilder();
    }

    /**
     * Creates a builder instance from an existing FinancialInstrumentIdentifier
     * @param identifier the existing identifier to copy from
     * @return new builder instance with values from the existing identifier
     */
    public static FinancialInstrumentIdentifierBuilder from(FinancialInstrumentIdentifier identifier) {
        return new FinancialInstrumentIdentifierBuilder()
                .withJbgid(identifier.getJbgid())
                .withIsin(identifier.getIsin())
                .withValor(identifier.getValor())
                .withJbvalor(identifier.getJbvalor())
                .withBbgid(identifier.getBbgid());
    }
}
