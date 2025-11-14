package com.juliusbaer.cmt.pat.common.services.ldap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "flowable.bjb.ldap")
@Component
public class LdapUserProperties {

    public String searchBase = "";
    public Map<String, String> userDefinitionGroupMap = new HashMap<>();

    public String getSearchBase() {
        return searchBase;
    }

    public Map<String, String> getUserDefinitionGroupMap() {
        return userDefinitionGroupMap;
    }
}
