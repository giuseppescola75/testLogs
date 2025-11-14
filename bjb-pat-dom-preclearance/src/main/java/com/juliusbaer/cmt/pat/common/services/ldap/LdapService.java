package com.juliusbaer.cmt.pat.common.services.ldap;


import com.juliusbaer.cmt.pat.common.services.ldap.PatDataWorker;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapGroupResponse;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapUserResponse;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(value = "bjb.ldap.mock-server.enabled", havingValue = "true")
public interface LdapService {

    LdapUserResponse getLdapUserByUserId(String userId);

    LdapGroupResponse getLdapUsersByGroup(String groupName);

    /**
     * @param query Part of a first name, last name, u-number or personnel no. Minimum 3 characters. You let it empty, you will receive a list of all workers! (optional)
     * @return 10 first matching users
     */
    LdapUserResponse searchLdapUsers(String query);

    List<PatDataWorker>  fetchEmployees(String query);

    List<PatDataWorker> fetchEmployeesByGroup(String groupName);
}

