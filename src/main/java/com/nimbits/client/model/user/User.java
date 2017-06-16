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


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.CommonFactory;
import com.nimbits.client.model.Entity;
import org.apache.commons.lang3.StringUtils;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@PersistenceCapable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class User extends Entity implements Serializable {

    private final static EntityType entityType = EntityType.user;

    @Persistent
    private String emailAddress;

    @Persistent
    private String password;

    @Persistent
    @JsonIgnore
    private String passwordSalt;

    @Persistent
    private String sessionId;

    @Persistent
    private String verifyToken;

    @Persistent
    private Long tokenTimestamp;

    @Persistent
    private Boolean emailAuthorised;

    @Persistent
    private Boolean verified;
    /**
     *
     */
    private static final long serialVersionUID = 5L;

    @SuppressWarnings("unused")
    private User() {
        super(entityType);
    }


    public User(String name, String description, String parent, String owner, String emailAddress,
                String password, String passwordSalt, String verifyToken,
                Long verifyTokenTimestamp, Boolean emailAuthorised, Boolean verified) {
        super(name, description, entityType, parent, owner);
        this.emailAddress = emailAddress;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.emailAuthorised = emailAuthorised;


        this.verifyToken = verifyToken;
        this.tokenTimestamp = verifyTokenTimestamp;
        this.sessionId = "";
        this.verified = verified;
    }


    @Override
    public void update(Entity update) {
        super.update(update);

    }

    public User(EntityType entityType) {
        super(entityType);
    }

    public String getPassword() {
        return password;
    }


    public String getPasswordSalt() {
        return passwordSalt;
    }


    public String getEmail() {
        return CommonFactory.createEmailAddress(StringUtils.isEmpty(emailAddress) ? getName() : this.emailAddress);
    }


    public void setEmail(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public void setVerifyToken(String token) {
        this.verifyToken = token;
    }

    public void setTokenTimestamp(Long date) {
        this.tokenTimestamp = date;
    }


    public String getVerifyToken() {
        return this.verifyToken;
    }

    public Long getTokenTimestamp() {
        return this.tokenTimestamp == null ? 0 : this.tokenTimestamp;
    }


    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }


    public void setPassword(String cryptPassword) {
        this.password = cryptPassword;
    }


    public String getSessionId() {
        return sessionId == null ? "" : sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public Boolean getEmailAuthorised() {
        return emailAuthorised;
    }

    public void setEmailAuthorised(Boolean emailAuthorised) {
        this.emailAuthorised = emailAuthorised;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;



        if (emailAddress != null ? !emailAddress.equals(user.emailAddress) : user.emailAddress != null)
            return false;


        return true;
    }


    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + emailAddress.hashCode();


        return result;
    }


    public static class Builder extends EntityBuilder {


        private String emailAddress;

        private String password;

        private String passwordSalt;

        private String verifyToken;

        private Long passwordResetTimestamp;

        private Boolean emailAuthorised;

        private Boolean verified;

        public Builder email(String emailAddress) {
            this.emailAddress = emailAddress;
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


        public Builder setverifyToken(String verifyToken) {
            this.verifyToken = verifyToken;
            return this;
        }

        public Builder setverifyTokenTimestamp(Long verifyTokenTimestamp) {
            this.passwordResetTimestamp = verifyTokenTimestamp;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }

        public User create() {

            if (name == null && emailAddress != null) {
                name = CommonFactory.createName(emailAddress, entityType);
            }
            if (parent == null && emailAddress != null) {
                parent = emailAddress;
            }


            return new User(name, description, parent, owner,
                    emailAddress, password, passwordSalt,
                    verifyToken, passwordResetTimestamp, emailAuthorised, verified);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(User u) {
            super.init(u);
            this.emailAddress = u.getEmail();
            this.password = u.getPassword();
            this.passwordSalt = u.getPasswordSalt();
            this.verifyToken = u.getVerifyToken();
            this.passwordResetTimestamp = u.getTokenTimestamp();
            this.emailAuthorised = u.getEmailAuthorised();
            this.verified = u.getVerified();
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }


        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }


        public Builder emailAuthorised(boolean emailAuthorised) {
            this.emailAuthorised = emailAuthorised;
            return this;
        }

        public Builder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

    }
}
