package com.juliusbaer.cmt.pat.jbEntities;

import com.fasterxml.jackson.databind.JsonNode;

public class TransformationItem {
    private int level;
    private String masterLegalEntity;
    private String countryIsoCode;
    private String shortname;
    private String name;
    private String country;
    private String legalRole;
    private int poid;
    private String status;

    public TransformationItem(JsonNode node) {
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMasterLegalEntity() {
        return masterLegalEntity;
    }

    public void setMasterLegalEntity(String masterLegalEntity) {
        this.masterLegalEntity = masterLegalEntity;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLegalRole() {
        return legalRole;
    }

    public void setLegalRole(String legalRole) {
        this.legalRole = legalRole;
    }

    public int getPoid() {
        return poid;
    }

    public void setPoid(int poid) {
        this.poid = poid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
