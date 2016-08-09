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

package com.nimbits.server.orm;

import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserSource;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable
public class UserEntity extends EntityStore implements User {


    @Persistent
    private String password;

    @Persistent
    private String passwordSalt;

    @Persistent
    private String source;

    @Persistent
    private Boolean isAdmin;

    @Persistent
    private String passwordResetToken;

    @Persistent
    private Long passwordResetTimestamp;


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getPasswordSalt() {
        return passwordSalt;
    }

    //all users with null source are from google.
    @Override
    public UserSource getSource() {
        return source == null ? UserSource.google : UserSource.valueOf(source);
    }


    @SuppressWarnings("unused")
    protected UserEntity() {
    }


    public UserEntity(final User entity) {
        super(entity);

        // this.id = entity.getOwner();
        update(entity);

    }


    @Override
    public EmailAddress getEmail() {
        return CommonFactory.createEmailAddress(getName().getValue());
    }


    @Override
    public void update(final Entity update) {
        super.update(update);
        final User u = (User) update;

        this.passwordSalt = u.getPasswordSalt();
        this.password = u.getPassword();
        this.source = u.getSource().name();
        this.isAdmin = u.getIsAdmin();
        this.passwordResetTimestamp = u.getPasswordResetTimestamp();
        this.passwordResetToken = u.getPasswordResetToken();

    }

    @Override
    public void validate(User user) {
        super.validate(user);

    }

    @Override
    public void init(Entity anEntity) {

    }

    @Override
    public boolean getIsAdmin() {
        return isAdmin == null ? false : isAdmin;
    }

    @Override
    public void setIsAdmin(final boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    @Override
    public void setEmail(EmailAddress emailAddress) {

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
        return passwordResetToken;
    }

    public Long getPasswordResetTimestamp() {
        return this.passwordResetTimestamp == null ? 0 : (this.passwordResetTimestamp);
    }

    @Override
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    @Override
    public void setPassword(String cryptPassword) {
        this.password = cryptPassword;
    }


}
