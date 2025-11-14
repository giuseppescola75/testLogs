package com.juliusbaer.cmt.pat.common.services.ldap;

import lombok.Getter;

@Getter
public class PatDataWorker {

    private String firstName;
    private String lastName;
    private String uid;
    private String personnelNumber;
    private String displayName;
    private String shortName;

    public PatDataWorker() {
    }

    public PatDataWorker(String id, String fistName, String lastName, String displayName, String personnelNumber, String shortName) {
        this.uid = id;
        this.firstName = fistName;
        this.lastName = lastName;
        this.personnelNumber = personnelNumber;
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public PatDataWorker withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PatDataWorker withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PatDataWorker withUid(String uid) {
        this.uid = uid;
        return this;
    }

    public PatDataWorker withPersonnelNumber(String personnelNumber) {
        this.personnelNumber = personnelNumber;
        return this;
    }

    public PatDataWorker withShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

}