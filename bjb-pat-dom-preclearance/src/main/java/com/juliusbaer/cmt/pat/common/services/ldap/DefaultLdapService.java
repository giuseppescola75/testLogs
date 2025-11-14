package com.juliusbaer.cmt.pat.common.services.ldap;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.juliusbaer.cmt.pat.financialInstrument.batch.BatchConfig;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapGroupResponse;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapPerson;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapUserResponse;

@Service
@ConditionalOnMissingBean(LdapService.class)
public class DefaultLdapService implements LdapService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLdapService.class);
    private static final Integer QUERY_MAX_DURATION_IN_SECONDS = 3000;
    private static final int QUERY_MAX_SEARCH_RESULT_SIZE = 10;

    private static final String OBJECTCLASS = "objectclass";
    private static final String PERSON = "person";
    private static final String MEMBER_OF = "memberOf";
    private static final String ACCOUNT_NAME = "sAMAccountName";
    private static final String COMMON_NAME = "cn";
    private static final String DISTINGUISHED_NAME = "dn";
    private static final String LAST_NAME = "sn";
    private static final String FIRST_NAME = "givenName";
    private static final String DISPLAY_NAME = "displayname";
    private static final String FULL_NAME = "name";
    private static final String UID = "uid";

    private final LdapTemplate ldapTemplate;
    private final LdapUserProperties ldapUserProperties;
    private final LdapService ldapService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapService.class);

    public DefaultLdapService(LdapTemplate ldapTemplate, LdapUserProperties ldapUserProperties, LdapService ldapService) {
        this.ldapTemplate = ldapTemplate;
        this.ldapUserProperties = ldapUserProperties;
        this.ldapService = ldapService;
    }

    @Override
    public LdapUserResponse getLdapUserByUserId(String userId) throws FlowableException {
        LOGGER.info("Searching for user in ldap with search property: {} and value {}", ACCOUNT_NAME, userId);
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter(OBJECTCLASS, PERSON));
        filter.and(new EqualsFilter(ACCOUNT_NAME, userId));

        LdapQuery ldapQuery = query().searchScope(SearchScope.SUBTREE).timeLimit(QUERY_MAX_DURATION_IN_SECONDS).base(ldapUserProperties.getSearchBase())
                .filter(filter);

        List<LdapPerson> result = ldapTemplate.search(ldapQuery, new PersonAttributesMapper());

        if (result.isEmpty()) {
            throw new FlowableException("user not found in ldap for search property: " + ACCOUNT_NAME + " and value " + userId);
        } else if (result.size() > 1) {
            throw new FlowableException("too many users found in ldap for search property: " + ACCOUNT_NAME + " and value " + userId);
        } else {
            return new LdapUserResponse(result);
        }
    }

    @Override
    public LdapGroupResponse getLdapUsersByGroup(String groupName) {
        LOGGER.info("Searching for users in ldap with group name: {}", groupName);
        ContainerCriteria ldapQuery = query().searchScope(SearchScope.SUBTREE).timeLimit(QUERY_MAX_DURATION_IN_SECONDS).base(ldapUserProperties.getSearchBase())
                .where(OBJECTCLASS).is(PERSON).and(query().where(MEMBER_OF).is(groupName));

        List<LdapPerson> resultList = ldapTemplate.search(ldapQuery, new PersonAttributesMapper());

        return new LdapGroupResponse(groupName, resultList);
    }

    @Override
    public LdapUserResponse searchLdapUsers(String query) {
        LOGGER.info("Searching for users in ldap with query: {}", query);
        ContainerCriteria ldapQuery = query().searchScope(SearchScope.SUBTREE).timeLimit(QUERY_MAX_DURATION_IN_SECONDS).countLimit(QUERY_MAX_SEARCH_RESULT_SIZE)
                .base(ldapUserProperties.getSearchBase()).where(OBJECTCLASS).is(PERSON);

        //TODO: check if we need to exclude inactive users

        if (StringUtils.isNotEmpty(query)) {
            ldapQuery = ldapQuery.and(
                    query().where(COMMON_NAME).like("*" + query + "*").or(DISTINGUISHED_NAME).like("*" + query + "*").or(FIRST_NAME).like("*" + query + "*")
                            .or(LAST_NAME).like("*" + query + "*").or(FULL_NAME).like("*" + query + "*").or(DISPLAY_NAME).like("*" + query + "*").or(UID)
                            .like("*" + query + "*").or(ACCOUNT_NAME).like("*" + query + "*"));
        }

        List<LdapPerson> resultList = ldapTemplate.search(ldapQuery, new PersonAttributesMapper());

        return new LdapUserResponse(resultList);
    }

    public List<PatDataWorker> fetchEmployees(String query) {
        LOGGER.info("Searching for users in ldap with query: {}", query);
        LdapUserResponse results = ldapService.searchLdapUsers(query);
        if (results.getUsers() == null) {
            return Collections.emptyList();
        } else {
            return results.getUsers().stream()
                    .map(p -> new PatDataWorker().withFirstName(p.getFirstname()).withLastName(p.getLastname()).withUid(p.getUserId())//FIXME: incorrect?
                            .withPersonnelNumber(p.getUserId()))//FIXME incorrect?
                    .collect(Collectors.toList());
        }
    }

    public List<PatDataWorker> fetchEmployeesByGroup(String groupName) {
        LOGGER.info("Searching for users in ldap with group name: {}", groupName);
        LdapGroupResponse results = ldapService.getLdapUsersByGroup(groupName);
        if (results.getUsersInGroup() == null) {
            return Collections.emptyList();
        } else {
            return results.getUsersInGroup().stream()
                    .map(p -> new PatDataWorker().withFirstName(p.getFirstname()).withLastName(p.getLastname()).withUid(p.getUserId())//FIXME: incorrect?
                            .withPersonnelNumber(p.getUserId()))//FIXME incorrect?
                    .collect(Collectors.toList());
        }
    }

    private static class PersonAttributesMapper implements AttributesMapper<LdapPerson> {

        private static final String COMPANY = "company";
        private static final String DEPARTMENT = "department";
        private static final String MAIL = "mail";
        private static final String MOBILE = "mobile";
        private static final String TELEPHONE_NUMBER = "telephoneNumber";
        private static final String EXTENSION_ATTRIBUTE_1 = "extensionAttribute1";

        public LdapPerson mapFromAttributes(Attributes attrs) throws NamingException {

            LOG.debug("Received the following attributes from ldap:");
            Collections.list(attrs.getAll()).forEach(attribute -> {
                try {
                    LOG.debug("Found attribute: {} with the value: {}", attribute.getID(), attribute.get().toString());
                } catch (NamingException e) {
                    LOG.debug("Found attribute: {} but without a value", attribute.getID(), e);
                }
            });
            LOG.debug("********************************************");

            LdapPerson person = new LdapPerson();
            if (attrs.get(FIRST_NAME) != null) {
                person.setFirstname((String) attrs.get(FIRST_NAME).get());
            }
            if (attrs.get(LAST_NAME) != null) {
                person.setLastname((String) attrs.get(LAST_NAME).get());
            }
            if (attrs.get(ACCOUNT_NAME) != null) {
                person.setUserId((String) attrs.get(ACCOUNT_NAME).get());
            }
            // company (JB AD terminology) translates to jb_shortname which later on will be mapped to jb_master_legal_entity
            if (attrs.get(COMPANY) != null) {
                person.setCompanyName((String) attrs.get(COMPANY).get());
            }

            if (attrs.get(DEPARTMENT) != null) {
                person.setDepartment((String) attrs.get(DEPARTMENT).get());
            }

            if (attrs.get(MAIL) != null) {
                person.setEmail((String) attrs.get(MAIL).get());
            }

            if (attrs.get(MOBILE) != null) {
                person.setMobileNumber((String) attrs.get(MOBILE).get());
            }

            if (attrs.get(TELEPHONE_NUMBER) != null) {
                person.setPhoneNumber((String) attrs.get(TELEPHONE_NUMBER).get());
            }

            if (attrs.get(FULL_NAME) != null) {
                person.setDisplayName((String) attrs.get(FULL_NAME).get());
            }

            if (attrs.get(EXTENSION_ATTRIBUTE_1) != null) {
                person.setCountry((String) attrs.get(EXTENSION_ATTRIBUTE_1).get());
            }

            if (attrs.get(MEMBER_OF) != null) {
                NamingEnumeration<?> memberOfList = attrs.get(MEMBER_OF).getAll();
                while (memberOfList.hasMore()) {
                    person.addMemberOf((String) memberOfList.next());
                }
            }
            return person;
        }
    }

}
