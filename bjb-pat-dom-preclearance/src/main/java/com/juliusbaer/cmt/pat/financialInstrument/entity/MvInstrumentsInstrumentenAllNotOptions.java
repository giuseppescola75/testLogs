package com.juliusbaer.cmt.pat.financialInstrument.entity;

import com.google.errorprone.annotations.Immutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mv_instruments_all_not_options")
@Immutable
public class MvInstrumentsInstrumentenAllNotOptions {

    @Id
    @Column(length = 36)
    private String id;                      // UUID generated

    @Column(name = "ass_jb_global_id", length = 64)
    private String assJbGlobalId;

    @Column(name = "ass_isin", length = 32)
    private String assIsin;

    @Column(name = "ass_valor_no", length = 32)
    private String assValorNo;

    @Column(name = "ass_mst_sec_id", length = 32)
    private String assMstSecId;

    @Column(name = "ass_bbgid", length = 32)
    private String assBbgid;

    @Column(name = "ass_issuer_no", length = 32)
    private String assIssuerNo;

    @Column(name = "ass_bjb_name_long_issuer", length = 256)
    private String assBjbNameLongIssuer;

    @Column(name = "ass_bjb_name_long_product", length = 256)
    private String assBjbNameLongProduct;

    @Column(name = "ass_bjb_name_short_product", length = 256)
    private String assBjbNameShortProduct;

    @Column(name = "ass_fic_industry_sector", length = 128)
    private String assFicIndustrySector;

    @Column(name = "ass_instrument_group", length = 128)
    private String assInstrumentGroup;

    @Column(name = "ass_fic_product_type", length = 128)
    private String assFicProductType;

    @Column(name = "ass_fund_esg_coverage", length = 64)
    private String assFundEsgCoverage;

    @Column(name = "ass_sfdr_category_type", length = 64)
    private String assSfdrCategoryType;

    @Column(name = "ass_status", length = 32)
    private String assStatus;

    @Column(name = "ass_country", length = 3)
    private String assCountry;

    @Column(name = "ass_fic_fix_income_sector", length = 128)
    private String assFicFixIncomeSector;

    @Column(name = "ass_mst_fund_id", length = 32)
    private String assMstFundId;
}
