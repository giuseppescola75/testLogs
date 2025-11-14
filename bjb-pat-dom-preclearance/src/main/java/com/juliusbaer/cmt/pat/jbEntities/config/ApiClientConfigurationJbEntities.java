package com.juliusbaer.cmt.pat.jbEntities.config;

import com.juliusbaer.cmt.pat.app.compliancejb.openapi.api.DefaultApi;
import com.juliusbaer.cmt.pat.app.compliancejb.openapi.client.ApiClient;
import com.juliusbaer.cmt.pat.jbEntities.ComplianceJbInstrumentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiClientConfigurationJbEntities {

    @Bean("jbEntitiesApiClient")
    public ApiClient apiClient(ComplianceJbInstrumentProperties props) {
        ApiClient client = new ApiClient();   // default RestTemplate
        client.setBasePath(props.getBaseUrl()); // already contains %20
        return client;
    }

    @Bean("jbEntitiesDefaultApi")
    public DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }
}