CREATE MATERIALIZED VIEW mv_instruments_all_not_options
    REFRESH COMPLETE ON DEMAND AS
SELECT id,ass_jb_global_id,ass_isin,ass_valor_no,ass_bbgid,ass_issuer_no,ass_bjb_name_long_issuer,ass_bjb_name_long_product,ass_bjb_name_short_product
FROM   financial_instruments
WHERE  (ass_instrument_group <> '9' ) ;