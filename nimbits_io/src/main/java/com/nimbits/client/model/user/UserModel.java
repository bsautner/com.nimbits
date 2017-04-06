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


import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityName;

import java.io.Serializable;


public class UserModel extends EntityModel implements Serializable, User {


    @Expose
    private String emailAddress;

    @Expose
    private Boolean isAdmin;

    private String password;

    private String passwordSalt;

    @Expose
    private String source;

    @Expose
    private String sessionId;

    private String passwordResetToken;

    private Long passwordResetTimestamp;
    /**
     *
     */
    private static final long serialVersionUID = 5L;

    @SuppressWarnings("unused")
    private UserModel() {
        super();
    }


    protected UserModel(String id, CommonIdentifier name, String description, EntityType entityType, String parent, String owner, String emailAddress,
                        Boolean isAdmin, String password, String passwordSalt, String source, String passwordResetToken,
                        Long passwordResetTokenTimestamp) {
        super(id, name, description, entityType, parent, owner);
        this.emailAddress = emailAddress;
        this.isAdmin = isAdmin;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.source = source;

        this.passwordResetToken = passwordResetToken;
        this.passwordResetTimestamp = passwordResetTokenTimestamp;
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

     @Override
    public void setEmail(EmailAddress emailAddress) {
        this.emailAddress = emailAddress.getValue();
    }


    @Override
    public void setPasswordResetToken(String token) {
        this.passwordResetToken = token;
    }

    public void setPasswordResetTimestamp(Long date) {
        this.passwordResetTimestamp = date;
    }

    @Override
    public String getPasswordResetToken() {
        return this.passwordResetToken;
    }

    public Long getPasswordResetTimestamp() {
        return this.passwordResetTimestamp == null ? 0 : this.passwordResetTimestamp;
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
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserModel userModel = (UserModel) o;


        if (isAdmin != userModel.isAdmin) return false;
        if (emailAddress != null ? !emailAddress.equals(userModel.emailAddress) : userModel.emailAddress != null)
            return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + emailAddress.hashCode();

        result = 31 * result + (isAdmin ? 1 : 0);

        return result;
    }

    public static class Builder extends EntityBuilder {
        private final EntityType type = EntityType.user;

        private String emailAddress;

        private Boolean isAdmin;

        private String password;

        private String passwordSalt;

        private String source;
        private String passwordResetToken;

        private Long passwordResetTimestamp;

        public Builder email(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }


        public Builder isAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
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


        public Builder setPasswordResetToken(String passwordResetToken) {
            this.passwordResetToken = passwordResetToken;
            return this;
        }

        public Builder setPasswordResetTokenTimestamp(Long passwordResetTokenTimestamp) {
            this.passwordResetTimestamp = passwordResetTokenTimestamp;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, type);
            return this;
        }

        public User create() {

            if (name == null && emailAddress != null) {
                name = CommonFactory.createName(emailAddress, type);
            }
            if (parent == null && emailAddress != null) {
                parent = emailAddress;
            }


            return new UserModel(id, name, description, type, parent, owner,
                    emailAddress, isAdmin, password, passwordSalt, source,  passwordResetToken, passwordResetTimestamp);
        }

        @Override
        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(User u) {
            super.init(u);
            this.emailAddress = u.getEmail().getValue();

            this.isAdmin = u.getIsAdmin();
            this.password = u.getPassword();
            this.passwordSalt = u.getPasswordSalt();
            this.source = u.getSource().name();

            this.passwordResetToken = u.getPasswordResetToken();
            this.passwordResetTimestamp = u.getPasswordResetTimestamp();
            return this;
        }

        @Override
        public Builder name(EntityName name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder description(String description) {
            this.description = description;
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
        public Builder action(String action) {
            this.action = action;
            return this;
        }
    }
}
