CREATE MATERIALIZED VIEW mv_instruments_instrumenten_equities_funds_digitalasset
    REFRESH COMPLETE ON DEMAND AS
SELECT id,ass_jb_global_id,ass_isin,ass_valor_no,ass_bbgid,ass_issuer_no,ass_bjb_name_long_issuer,ass_bjb_name_long_product,ass_bjb_name_short_product
FROM   financial_instruments
WHERE   (ass_instrument_group = '1' OR ass_instrument_group ='11' OR ass_instrument_group ='5') -- your literal