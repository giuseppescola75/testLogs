package com.juliusbaer.cmt.pat.common.services.ldap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "The response for the get ldap users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LdapUserResponse {

    @ApiModelProperty(example = "List<Person>", required = true)
    private List<LdapPerson> users;

    public LdapUserResponse() {
    }

    public LdapUserResponse(List<LdapPerson> users) {
        this.users = users;
    }

    public List<LdapPerson> getUsers() {
        return users;
    }

    public void setUsers(List<LdapPerson> users) {
        this.users = users;
    }
}

