package com.juliusbaer.cmt.pat.preClearance.config;

import com.juliusbaer.cmt.pat.app.activoin.openapi.api.DefaultApi;
import com.juliusbaer.cmt.pat.app.activoin.openapi.client.ApiClient;
import com.juliusbaer.cmt.pat.preClearance.validatePreClearance.PreClearanceApiProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class ApiClientConfiguration {

    @Bean("preClearanceApiClient")
    public ApiClient preClearanceApiClient(PreClearanceApiProperties properties) {
        // 1. Create a RestTemplate that will be injected
        RestTemplate customRestTemplate = new RestTemplate();

        // 2. Create a URI factory set to EncodingMode.NONE
        // This tells the RestTemplate: "Do not encode URLs. Ever."
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        // 3. Apply this factory to our custom RestTemplate
        customRestTemplate.setUriTemplateHandler(factory);

        // 4. Create the ApiClient using the constructor that accepts a RestTemplate.
        // This bypasses the broken internal buildRestTemplate() method.
        ApiClient apiClient = new ApiClient(customRestTemplate);

        // 5. Set the BASE path using the RAW, UNENCODED URL.
        // The RestTemplate will now use this string as-is, with the literal space.
        apiClient.setBasePath(properties.getBaseUrl());

        return apiClient;
    }

    @Bean("preClearanceDefaultApi")
    public DefaultApi defaultApi(@Qualifier("preClearanceApiClient") ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }

}