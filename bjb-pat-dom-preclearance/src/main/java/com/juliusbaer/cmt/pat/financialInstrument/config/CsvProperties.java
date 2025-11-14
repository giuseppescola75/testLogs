package com.juliusbaer.cmt.pat.financialInstrument.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bjb.pat.financialinstrument.csv")
public class CsvProperties {
    private String filePath = "dummyPath";
    // Constants for table and column names
    private final String tableName = "FINANCIAL_INSTRUMENTS";
    private final String[] columnNames = {
            "ass_jb_global_id", "ass_isin", "ass_valor_no",
            "ass_mst_sec_id", "ass_bbgid", "ass_issuer_no",
            "ass_bjb_name_long_issuer", "ass_bjb_name_long_product",
            "ass_bjb_name_short_product", "ass_fic_industry_sector",
            "ass_instrument_group", "ass_fic_product_type",
            "ass_fund_esg_coverage", "ass_sfdr_category_type",
            "ass_status", "ass_country",
            "ass_fic_fix_income_sector", "ass_mst_fund_id"
    };

    private final String[] tableColumnNames = {
            "id","ass_jb_global_id", "ass_isin", "ass_valor_no",
            "ass_mst_sec_id", "ass_bbgid", "ass_issuer_no",
            "ass_bjb_name_long_issuer", "ass_bjb_name_long_product",
            "ass_bjb_name_short_product", "ass_fic_industry_sector",
            "ass_instrument_group", "ass_fic_product_type",
            "ass_fund_esg_coverage", "ass_sfdr_category_type",
            "ass_status", "ass_country",
            "ass_fic_fix_income_sector", "ass_mst_fund_id"
    };

    private String cronExpression;
    private int chunk;
    private boolean enabled;
}