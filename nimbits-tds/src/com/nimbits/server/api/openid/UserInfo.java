package com.nimbits.server.api.openid;


import java.io.Serializable;

/**
 * Simple representation of an authenticated user.
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String claimedId;
    private String email;
    private String firstName;
    private String lastName;

    public UserInfo() {
    }

    public UserInfo(String claimedId, String email, String firstName,
                    String lastName) {
        this.claimedId = claimedId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getClaimedId() {
        return claimedId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
