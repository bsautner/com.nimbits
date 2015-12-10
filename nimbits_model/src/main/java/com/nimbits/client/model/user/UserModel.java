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
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserModel extends EntityModel implements Serializable, User {



    @Expose
    private String emailAddress;

    private List<AccessKey> accessKeys;

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
    private static final long serialVersionUID = 5L;

    @SuppressWarnings("unused")
    public UserModel() {
        super();
    }


    public UserModel(String key, CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String uuid, String emailAddress,
                     List<AccessKey> accessKeys, Boolean isAdmin, String token, String password, String passwordSalt, String source, LoginInfo loginInfo, String passwordResetToken,
                     Date passwordResetTokenTimestamp) {
        super(key, name, description, entityType, protectionLevel, parent, owner, uuid);
        this.emailAddress = emailAddress;
        this.accessKeys = accessKeys;
        this.isAdmin = isAdmin;
        this.token = token;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.source = source;
        this.loginInfo = loginInfo;
        this.passwordResetToken = passwordResetToken;
        this.passwordResetTokenTimestamp = passwordResetTokenTimestamp;
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


        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + emailAddress.hashCode();
        result = 31 * result + (accessKeys != null ? accessKeys.hashCode() : 0);

        result = 31 * result + (isAdmin ? 1 : 0);

        return result;
    }

    public static class Builder extends EntityBuilder {


        private String emailAddress;

        private List<AccessKey> accessKeys;

        private Boolean isAdmin;

        private String token;

        private String password;

        private String passwordSalt;

        private String source;

        private LoginInfo loginInfo;

        private String passwordResetToken;

        private Date passwordResetTokenTimestamp;

        public Builder email(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setAccessKeys(List<AccessKey> accessKeys) {
            this.accessKeys = accessKeys;
            return this;
        }

        public Builder setIsAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder salt(String passwordSalt) {
            this.passwordSalt = passwordSalt;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder setLoginInfo(LoginInfo loginInfo) {
            this.loginInfo = loginInfo;
            return this;
        }

        public Builder setPasswordResetToken(String passwordResetToken) {
            this.passwordResetToken = passwordResetToken;
            return this;
        }

        public Builder setPasswordResetTokenTimestamp(Date passwordResetTokenTimestamp) {
            this.passwordResetTokenTimestamp = passwordResetTokenTimestamp;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, EntityType.user);
            return this;
        }

        public User create() {
            if (protectionLevel == null) {
                protectionLevel = ProtectionLevel.everyone;
            }


            return new UserModel(key, name, description, EntityType.user, protectionLevel, parent, owner, uuid,
                    emailAddress, accessKeys,isAdmin, token, password, passwordSalt, source, loginInfo, passwordResetToken, passwordResetTokenTimestamp  );
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        @Override
        public Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        private void initEntity(Entity anEntity) {

            this.key = anEntity.getKey();
            this.id = anEntity.getKey();
            this.name = anEntity.getName();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel();
            this.alertType = anEntity.getAlertType().getCode();
            this.uuid = anEntity.getUUID();

        }

        public Builder init(User u) {
            initEntity(u);
            this.emailAddress = u.getEmail().getValue();
            this.accessKeys = u.getAccessKeys();
            this.isAdmin = u.getIsAdmin();
            this.token = u.getToken();
            this.password = u.getPassword();
            this.passwordSalt = u.getPasswordSalt();
            this.source = u.getSource().name();
            this.loginInfo = u.getLoginInfo();
            this.passwordResetToken = u.getPasswordResetToken();
            this.passwordResetTokenTimestamp = u.getPasswordResetTokenTimestamp();
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }
        @Override
        public Builder key(String key) {
            this.key = key;
            return this;
        }
        @Override
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        @Override
        public Builder protectionLevel(ProtectionLevel protectionLevel) {
            this.protectionLevel = protectionLevel;
            return this;
        }
        @Override
        public Builder alertType(int alertType) {
            this.alertType = alertType;
            return this;
        }
        @Override
        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }
        @Override
        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }
        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        @Override
        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public Builder action(String action) {
            this.action = action;
            return this;
        }
    }
}
