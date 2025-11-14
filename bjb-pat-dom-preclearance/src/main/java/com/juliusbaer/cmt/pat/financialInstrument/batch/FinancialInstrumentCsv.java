package com.juliusbaer.cmt.pat.financialInstrument.batch;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class FinancialInstrumentCsv {

    @CsvBindByName(column = "ASS-JB-GLOBAL-ID")
    private String assJbGlobalId;

    @CsvBindByName(column = "ASS-ISIN")
    private String assIsin;

    @CsvBindByName(column = "ASS-VALOR-NO")
    private String assValorNo;

    @CsvBindByName(column = "ASS-MST-SEC-ID")
    private String assMstSecId;

    @CsvBindByName(column = "ASS-BBGID")
    private String assBbgid;

    @CsvBindByName(column = "ASS-ISSUER-NO")
    private String assIssuerNo;

    @CsvBindByName(column = "ASS-BJB-NAME-LONG-ISSUER")
    private String assBjbNameLongIssuer;

    @CsvBindByName(column = "ASS-BJB-NAME-LONG-PRODUCT")
    private String assBjbNameLongProduct;

    @CsvBindByName(column = "ASS-BJB-NAME-SHORT-PRODUCT")
    private String assBjbNameShortProduct;

    @CsvBindByName(column = "ASS-FIC-INDUSTRYSECTOR")
    private String assFicIndustrySector;

    @CsvBindByName(column = "ASS-INSTRUMENT-GROUP")
    private String assInstrumentGroup;

    @CsvBindByName(column = "ASS-FIC-PRODUCTTYPE")
    private String assFicProductType;

    @CsvBindByName(column = "ASS-FUND-ESG-COVERAGE")
    private String assFundEsgCoverage;

    @CsvBindByName(column = "ASS-SFDR-CATEGORY-TYPE")
    private String assSfdrCategoryType;

    @CsvBindByName(column = "ASS-STATUS")
    private String assStatus;

    @CsvBindByName(column = "ASS-COUNTRY")
    private String assCountry;

    @CsvBindByName(column = "ASS-FIC-FIXINCOMESECTOR")
    private String assFicFixIncomeSector;

    @CsvBindByName(column = "ASS-MST-FUND-ID")
    private String assMstFundId;
}
