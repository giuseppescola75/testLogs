package com.juliusbaer.cmt.pat.financialInstrument.dto;


import java.util.Objects;

public class FinancialInstrumentDropdownDto {

    private final String assBjbNameLongIssuer;
    private final String isin;
    private final String valorNo;
    private final String bbgId;
    private final String jbGlobalId;

    public FinancialInstrumentDropdownDto(String assBjbNameLongIssuer, String isin, String valorNo, String bbgId, String jbGlobalId) {
        this.assBjbNameLongIssuer = assBjbNameLongIssuer;
        this.isin = isin;
        this.valorNo = valorNo;
        this.bbgId = bbgId;
        this.jbGlobalId = jbGlobalId;
    }

    public String getAssBjbNameLongIssuer() {
        return assBjbNameLongIssuer;
    }

    public String getIsin() {
        return isin;
    }

    public String getValorNo() {
        return valorNo;
    }

    public String getBbgId() {
        return bbgId;
    }

    public String getJbGlobalId() {
        return jbGlobalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinancialInstrumentDropdownDto that)) return false;
        return Objects.equals(assBjbNameLongIssuer, that.assBjbNameLongIssuer) &&
                Objects.equals(isin, that.isin) &&
                Objects.equals(valorNo, that.valorNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assBjbNameLongIssuer, isin, valorNo);
    }

    @Override
    public String toString() {
        return "FinancialInstrumentDropdownDto{" +
                "assBjbNameLongIssuer='" + assBjbNameLongIssuer + '\'' +
                ", isin='" + isin + '\'' +
                ", valorNo='" + valorNo + '\'' +
                '}';
    }
}
