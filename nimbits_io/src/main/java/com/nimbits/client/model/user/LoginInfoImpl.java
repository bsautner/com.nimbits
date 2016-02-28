/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
