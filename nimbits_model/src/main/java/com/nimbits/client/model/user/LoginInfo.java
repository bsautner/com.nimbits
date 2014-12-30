package com.nimbits.client.model.user;

import java.io.Serializable;

public interface LoginInfo extends Serializable {

    String getLoginUrl();

    void setLoginUrl(String loginUrl);

    String getLogoutUrl();

    void setLogoutUrl(String logoutUrl);

    UserStatus getUserStatus();

    boolean isGAE();

}
