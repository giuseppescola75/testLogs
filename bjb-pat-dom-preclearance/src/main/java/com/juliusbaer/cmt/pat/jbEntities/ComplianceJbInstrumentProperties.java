package com.juliusbaer.cmt.pat.jbEntities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bjb.pat.compliancejbinstrument.api")
public class ComplianceJbInstrumentProperties {
    private String baseUrl;

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
