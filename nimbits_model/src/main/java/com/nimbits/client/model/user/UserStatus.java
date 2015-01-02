package com.nimbits.client.model.user;

import java.io.Serializable;

public enum UserStatus implements Serializable {

    newServer, newUser, loggedIn, unknown;

    UserStatus() {
    }
}
