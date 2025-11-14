CREATE INDEX idx_mv_ig_id    ON mv_instruments_instrumenten_equities (LOWER(id));
CREATE INDEX idx_mv_ig_product ON mv_instruments_instrumenten_equities (LOWER(ass_bjb_name_long_product));
CREATE INDEX idx_mv_ig_ass_isin ON mv_instruments_instrumenten_equities (LOWER(ass_isin));



CREATE INDEX idx_mv_fd_id    ON mv_instruments_instrumenten_equities_funds_digitalasset (LOWER(id));
CREATE INDEX idx_mv_fd_product ON mv_instruments_instrumenten_equities_funds_digitalasset (LOWER(ass_bjb_name_long_product));
CREATE INDEX idx_mv_fd_ass_isin ON mv_instruments_instrumenten_equities_funds_digitalasset (LOWER(ass_isin));
