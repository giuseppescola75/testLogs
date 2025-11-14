package com.juliusbaer.cmt.pat.common.services.ldap.mapper;

import com.flowable.core.idm.api.PlatformUser;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapGroupResponse;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapPerson;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapUserResponse;


import java.util.ArrayList;
import java.util.List;

public class LdapFlowableUserMapper {

    private final String DEFAULT_COUNTRY = "Country";
    private final String DEFAULT_DEPARTMENT = "Department X";
    private final String DEFAULT_COMPANY_NAME = "Company Y";
    private final String DEFAULT_MOBILE_NUMBER = "+41791111111";
    private final String DEFAULT_PHONE_NUMBER = "+41791111111";

    public LdapPerson buildLdapPerson(
            String firstName,
            String lastName,
            String displayName,
            String login,
            String email
    ){
        LdapPerson person = new LdapPerson();
        person.setUserId(login);
        person.setLastname(lastName);
        person.setFirstname(firstName);
        person.setEmail(email);
        person.setCountry(DEFAULT_COUNTRY);
        person.setDepartment(DEFAULT_DEPARTMENT);
        person.setDisplayName(displayName);
        person.setCompanyName(DEFAULT_COMPANY_NAME);
        person.setMobileNumber(DEFAULT_MOBILE_NUMBER);
        person.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        return person;
    }

    public LdapPerson buildLdapPerson(PlatformUser platformUser){
        return buildLdapPerson(
                platformUser.getFirstName(),
                platformUser.getLastName(),
                platformUser.getDisplayName(),
                platformUser.getId(),
                platformUser.getEmail()
        );
    }

    public LdapUserResponse buildLdapUserResponse(List<PlatformUser> platformUsers){
        List<LdapPerson> personList = new ArrayList<>();
        for (PlatformUser platformUser : platformUsers){
            personList.add(buildLdapPerson(platformUser));
        }
        return new LdapUserResponse(personList);
    }

    public LdapGroupResponse buildLdapGroupResponse(List<PlatformUser> platformUsers, String groupName){
        List<LdapPerson> personList = new ArrayList<>();
        for (PlatformUser platformUser : platformUsers){
            personList.add(buildLdapPerson(platformUser));
        }
        return new LdapGroupResponse(groupName, personList);
    }

}
