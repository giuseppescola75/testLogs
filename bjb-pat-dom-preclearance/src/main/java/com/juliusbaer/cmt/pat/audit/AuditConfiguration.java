package com.juliusbaer.cmt.pat.audit;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.parse.BpmnParseHandler;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flowable.spring.boot.EngineConfigurationConfigurer;

@Configuration
public class AuditConfiguration {

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> AuditProcessEngineConfigurationConfigurer() {
        return processEngineConfiguration -> {
            List<BpmnParseHandler> parseHandlers = processEngineConfiguration.getPostBpmnParseHandlers();
            if (parseHandlers == null) {
                parseHandlers = new ArrayList<>();
            }
            parseHandlers.add(new PcrAuditBpmnParseHandler());
            processEngineConfiguration.setPostBpmnParseHandlers(parseHandlers);
        };
    }
}
