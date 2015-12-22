package com.nimbits.client.model.user;

import java.io.Serializable;

public class LoginInfoImpl implements Serializable, LoginInfo {


    private String loginUrl;

    private String logoutUrl;

    private UserStatus userStatus;

    private boolean isGAE;


    public LoginInfoImpl(String loginUrl, String logoutUrl, UserStatus userStatus, boolean isGAE) {

        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
        this.userStatus = userStatus;
        this.isGAE = isGAE;
    }

    public LoginInfoImpl() {
    }

    @Override
    public String getLoginUrl() {
        return loginUrl;
    }

    @Override
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @Override
    public String getLogoutUrl() {
        return logoutUrl;
    }

    @Override
    public void setLogoutUrl(final String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    @Override
    public UserStatus getUserStatus() {
        return this.userStatus == null ? UserStatus.unknown : userStatus;
    }

    @Override
    public boolean isGAE() {
        return isGAE;
    }

}
