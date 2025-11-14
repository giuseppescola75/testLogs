package com.juliusbaer.cmt.pat.common.services.ldap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LdapPerson implements Serializable {

    private String userId;
    private String companyName;
    private String department;
    private String email;
    private String firstname;
    private String lastname;
    private String displayName;
    private String mobileNumber;
    private String phoneNumber;
    private String country;
    private List<String> memberOfList = new ArrayList<>();

    public LdapPerson() {
    }

    public LdapPerson(String userId, String companyName, String department, String email, String firstname, String lastname, String displayName,
                      String mobileNumber, String phoneNumber, String country, List<String> memberOfList) {
        this.userId = userId;
        this.companyName = companyName;
        this.department = department;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.displayName = displayName;
        this.mobileNumber = mobileNumber;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.memberOfList = memberOfList;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public void setMemberOfList(List<String> memberOfList) {
        this.memberOfList = memberOfList;
    }

    public List<String> getMemberOfList() {
        return memberOfList;
    }

    public void addMemberOf(String memberOf) {
        memberOfList.add(memberOf);
    }

}


