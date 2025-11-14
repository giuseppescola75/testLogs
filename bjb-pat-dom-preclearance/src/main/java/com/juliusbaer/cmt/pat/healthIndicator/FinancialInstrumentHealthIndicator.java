package com.juliusbaer.cmt.pat.healthIndicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("financialInstruments")
public class FinancialInstrumentHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbcTemplate;

    public FinancialInstrumentHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        // Define table name as a constant
        final String tableName = "FINANCIAL_INSTRUMENTS";

        try {
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Long.class);

            // Check if the count is null or 0
            if (count == null || count == 0) {
                return Health.down()
                        .withDetail("table", tableName)
                        .withDetail("rowCount", count == null ? "NULL" : 0)
                        .withDetail("message", "No records found in " + tableName)
                        .build();
            }

            // If count is positive, return UP
            return Health.up()
                    .withDetail("table", tableName)
                    .withDetail("rowCount", count)
                    .build();

        } catch (DataAccessException e) {
            // If the query fails, return DOWN
            return Health.down()
                    .withDetail("table", tableName)
                    .withException(e) // This adds the exception message to the details
                    .build();
        }
    }
}
