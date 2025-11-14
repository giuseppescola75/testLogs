package com.juliusbaer.cmt.pat.common.services.ldap;

import com.flowable.core.idm.api.PlatformIdentityService;
import com.flowable.core.idm.api.PlatformUser;
import com.juliusbaer.cmt.pat.common.services.ldap.mapper.LdapFlowableUserMapper;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapGroupResponse;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapUserResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "bjb.ldap.mock-server.enabled", havingValue = "true")
public class MockLdapService implements LdapService {

    private final PlatformIdentityService platformIdentityService;

    public MockLdapService(PlatformIdentityService platformIdentityService) {
        this.platformIdentityService = platformIdentityService;
    }

    @Override
    public LdapUserResponse getLdapUserByUserId(String userId) {
        List<PlatformUser> matchingUsers = platformIdentityService.createPlatformUserQuery().includeIdentityInfo().userId(userId).list();
        return new LdapFlowableUserMapper().buildLdapUserResponse(matchingUsers);
    }

    @Override
    public LdapGroupResponse getLdapUsersByGroup(String groupName) {
        List<PlatformUser> matchingUsers = platformIdentityService.createPlatformUserQuery().includeIdentityInfo().memberOfGroupKey(groupName).list();
        return new LdapFlowableUserMapper().buildLdapGroupResponse(matchingUsers, groupName);
    }

    @Override
    public LdapUserResponse searchLdapUsers(String query) {
        List<PlatformUser> matchingUsers = platformIdentityService.createPlatformUserQuery().includeIdentityInfo().userDisplayNameLikeIgnoreCase("%" + query + "%").list();
        return new LdapFlowableUserMapper().buildLdapUserResponse(matchingUsers);
    }

    @Override
    public List<com.juliusbaer.cmt.pat.common.services.ldap.PatDataWorker> fetchEmployees(String query) {
        return List.of();
    }

    @Override
    public List<com.juliusbaer.cmt.pat.common.services.ldap.PatDataWorker> fetchEmployeesByGroup(String groupName) {
        List<PlatformUser> matchingUsers = platformIdentityService.createPlatformUserQuery().includeIdentityInfo().memberOfGroupKey(groupName).list();
        return matchingUsers.stream().map(this::mapToPatDataWorker) // Use the same helper method
                .toList();
    }

    private PatDataWorker mapToPatDataWorker(PlatformUser user) {
        // CORRECT MOCK LOGIC: Derive the short name from the user's ID (which comes from the "login" field in the JSON).
        String userId = user.getId();
        String shortName = userId; // Default to the full ID
        if (userId != null && userId.contains("@")) {
            shortName = userId.split("@")[0];
        }

        return new PatDataWorker(userId, user.getFirstName(), user.getLastName(), user.getDisplayName(), user.getId(), shortName);
    }
}

