/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.user;


import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserModel extends EntityModel implements Serializable, User {


    private Date lastLoggedIn;

    @Expose
    private String emailAddress;

    private ArrayList<AccessKey> accessKeys;

    @Expose
    private Boolean isAdmin;

    @Expose
    private String token;

    private String password;

    private String passwordSalt;

    @Expose
    private String source;

    private LoginInfo loginInfo;

    private String passwordResetToken;

    private Date passwordResetTokenTimestamp;
    /**
     *
     */
    private static final long serialVersionUID = 4L;

    @SuppressWarnings("unused")
    public UserModel() {
        super();
    }

    public UserModel(final User u) {
        super(u);
        if (u != null) {
            this.lastLoggedIn = u.getLastLoggedIn();
            this.accessKeys = (ArrayList<AccessKey>) u.getAccessKeys();
            this.emailAddress = u.getEmail().getValue();
            this.password = u.getPassword();
            this.passwordSalt = u.getPasswordSalt();
            this.source = u.getSource().name();
            this.isAdmin = u.getIsAdmin();
            this.passwordResetTokenTimestamp = u.getPasswordResetTokenTimestamp();
            this.passwordResetToken = u.getPasswordResetToken();


        }
    }

    public UserModel(final EntityName name,
                     final String description,
                     final EntityType entityType,
                     final ProtectionLevel protectionLevel,
                     final String parent,
                     final String owner) {
        super(name, description, entityType, protectionLevel, parent,
                owner, "");
        this.lastLoggedIn = new Date();
        this.emailAddress = name.getValue();



    }

    public UserModel(final EntityName name,
                     final String description,
                     final EntityType entityType,
                     final ProtectionLevel protectionLevel,
                     final String parent,
                     final String owner, final String password, final String salt, final UserSource source) {
        super(name, description, entityType, protectionLevel, parent, owner, "");
        this.lastLoggedIn = new Date();
        this.emailAddress = name.getValue();
        this.password = password;
        this.passwordSalt = salt;
        this.source = source.name();

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getPasswordSalt() {
        return passwordSalt;
    }

    @Override
    public UserSource getSource() {
        return source == null ? UserSource.google : UserSource.valueOf(source);
    }

    @Override
    public Date getLastLoggedIn() {
        return new Date(lastLoggedIn.getTime());
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {

        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }


    @Override
    public boolean isRestricted() {
        if (accessKeys == null) {
            return true;
        }
        for (final AccessKey key : accessKeys) {
            if (key.getAuthLevel().getCode() > (AuthLevel.restricted.getCode())) {
                return false;
            }
        }
        return true;

    }

    @Override
    public List<AccessKey> getAccessKeys() {
        return this.accessKeys == null ? new ArrayList<AccessKey>(1) : this.accessKeys;
    }

    @Override
    public void addAccessKey(final AccessKey key) {
        if (accessKeys == null) {
            accessKeys = new ArrayList<AccessKey>(1);
        }
        accessKeys.add(key);
    }


    @Override
    public boolean getIsAdmin() {
        return isAdmin == null ? false : isAdmin;
    }

    @Override
    public void setIsAdmin(final boolean userAdmin) {
        this.isAdmin = userAdmin;
    }

    @Override
    public EmailAddress getEmail() {
        return CommonFactory.createEmailAddress(emailAddress);
    }

    public String getToken() {
        return token;
    }

    @Override
    public void setEmail(EmailAddress emailAddress) {
        this.emailAddress = emailAddress.getValue();
    }

    @Override
    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    @Override
    public LoginInfo getLoginInfo() {
        return loginInfo == null ? UserModelFactory.createNullLoginInfo(false) : loginInfo;
    }

    @Override
    public void setPasswordResetToken(String token) {
        this.passwordResetToken = token;
    }

    @Override
    public void setPasswordResetTokenTimestamp(Date date) {
        this.passwordResetTokenTimestamp = date;
    }

    @Override
    public String getPasswordResetToken() {
        return this.passwordResetToken;
    }

    @Override
    public Date getPasswordResetTokenTimestamp() {
        return this.passwordResetTokenTimestamp == null ? new Date(0) : this.passwordResetTokenTimestamp;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    @Override
    public void setPassword(String cryptPassword) {
        this.password = cryptPassword;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserModel userModel = (UserModel) o;


        if (isAdmin != userModel.isAdmin) return false;
        if (accessKeys != null ? !accessKeys.equals(userModel.accessKeys) : userModel.accessKeys != null) return false;
        if (emailAddress != null ? !emailAddress.equals(userModel.emailAddress) : userModel.emailAddress != null)
            return false;
        if (lastLoggedIn != null ? !lastLoggedIn.equals(userModel.lastLoggedIn) : userModel.lastLoggedIn != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lastLoggedIn != null ? lastLoggedIn.hashCode() : 0);
        result = 31 * result + emailAddress.hashCode();
        result = 31 * result + (accessKeys != null ? accessKeys.hashCode() : 0);

        result = 31 * result + (isAdmin ? 1 : 0);

        return result;
    }
}
