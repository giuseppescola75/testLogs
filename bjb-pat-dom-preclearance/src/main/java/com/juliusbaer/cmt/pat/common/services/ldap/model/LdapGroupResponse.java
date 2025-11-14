package com.juliusbaer.cmt.pat.common.services.ldap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juliusbaer.cmt.pat.common.services.ldap.model.LdapPerson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "The response for the get ldap users by group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LdapGroupResponse {

    @ApiModelProperty(example = "administrator", required = true)
    private String groupName;

    @ApiModelProperty(example = "List<Person>", required = true)
    private List<LdapPerson> usersInGroup;

    public LdapGroupResponse() {
    }

    public LdapGroupResponse(String groupName, List<LdapPerson> usersInGroup) {
        this.groupName = groupName;
        this.usersInGroup = usersInGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<LdapPerson> getUsersInGroup() {
        return usersInGroup;
    }

    public void setUsersInGroup(List<LdapPerson> usersInGroup) {
        this.usersInGroup = usersInGroup;
    }
}
