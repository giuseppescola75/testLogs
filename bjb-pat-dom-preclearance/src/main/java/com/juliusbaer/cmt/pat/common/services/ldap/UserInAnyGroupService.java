package com.juliusbaer.cmt.pat.common.services.ldap;

import com.flowable.core.idm.api.PlatformIdentityService;
import com.flowable.core.idm.api.PlatformUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userInAnyGroupService")
@SuppressWarnings("unused")
public class UserInAnyGroupService {

    private final PlatformIdentityService platformIdentityService;

    public UserInAnyGroupService(PlatformIdentityService platformIdentityService) {
        this.platformIdentityService = platformIdentityService;
    }

    public Boolean isUserInAnyGroup(String userId, String groupName) {
        List<PlatformUser> list = platformIdentityService.createPlatformUserQuery().memberOfGroupKey(groupName).list();
        return list.stream().anyMatch(user -> amendBjbUserDomain(user.getId()).equals(amendBjbUserDomain(userId)));
    }

    private String amendBjbUserDomain(String userId) {
        if (!userId.contains("@juliusbaer.com")) {
            return userId.concat("@juliusbaer.com");
        }
        return userId;
    }

}
