package com.veeva.vault.custom.udc;

import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;

@UserDefinedClassInfo
public class VpsAccountData {
    private String firstName;
    private String lastName;
    private String credentials;

    public VpsAccountData() {
    }

    public VpsAccountData(String firstName, String lastName, String credentials) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.credentials = credentials;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}