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

package com.nimbits.server.transaction.user.service;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.Credentials;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.dao.UserDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

@Service
public class UserService {

    private final UserDao userDao;

    private final EntityDao entityDao;


    @Autowired
    public UserService(UserDao userDao, EntityDao entityDao) {
        this.userDao = userDao;
        this.entityDao = entityDao;

    }


    private Optional<Credentials> credentialsWithBasicAuthentication(String authHeader) {

        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                if (basic.equalsIgnoreCase("Basic")) {
                    try {
                        // String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
                        String credentials = st.nextToken();

                        int p = credentials.indexOf(":");
                        if (p != -1) {
                            String login = credentials.substring(0, p).trim();
                            String password = credentials.substring(p + 1).trim();

                            return Optional.of(new Credentials(login, password));
                        } else {
                            return Optional.absent();

                        }
                    } catch (Exception e) {
                        return Optional.absent();

                    }
                }
            }
        }

        return Optional.absent();
    }


    public User createUserRecord(final EmailAddress internetAddress, String password, UserSource source) {
        final EntityName name = CommonFactory.createName(internetAddress.getValue(), EntityType.user);
        String passwordSalt = RandomStringUtils.randomAscii(20);
        String cryptPassword = DigestUtils.sha512Hex(password + passwordSalt);
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Attempt to create a user with a null password.");
        }


        boolean isFirst = !userDao.usersExist();

        final User newUser = new UserModel.Builder()
                .name(name)
                .password(cryptPassword)
                .salt(passwordSalt)
                .source(source.name())
                .email(internetAddress.getValue())
                .owner(internetAddress.getValue())
                .parent(internetAddress.getValue())
                .create();


        newUser.setIsAdmin(isFirst);

        return (User) entityDao.addEntity(newUser, newUser);

    }



    public Optional<User> getUserByKey(final String key) {
        User admin = userDao.getAdmin();
        Optional<Entity> optional = entityDao.getEntity(admin, key, EntityType.user);
        if (optional.isPresent()) {
            return Optional.of((User) optional.get());
        } else {
            return Optional.absent();
        }



    }

    private boolean validatePassword(User user, String password) {

        String storedEncodedPassword = user.getPassword();
        String salt = user.getPasswordSalt();
        String challenge = DigestUtils.sha512Hex(password + salt);

        boolean validPassword =  !StringUtils.isEmpty(password) && storedEncodedPassword.equals(challenge);
        boolean validSession = false;
        if (! validPassword) {
            validSession = userDao.validSession(user, password);
        }

        return validPassword || validSession;

    }

    public User getUser(String authString) {

        Optional<Credentials> credentials = credentialsWithBasicAuthentication(authString);
        if (credentials.isPresent()) {
            Optional<User> user = userDao.getUserByEmail(credentials.get().getLogin());
            if (user.isPresent()) {
                if (validatePassword(user.get(), credentials.get().getPassword())) {
                    return user.get();
                } else {

                    throw new SecurityException("Invalid Password: " + authString);
                }

            } else {
                throw new SecurityException("User Not Found: " + authString);
            }
        } else {
            throw new SecurityException("Invalid Credentials: " + authString);
        }


    }



    Optional<User> doLogin(String email, String password, boolean rm) {

        Optional<User> optional = userDao.getUserByEmail(email);

        if (optional.isPresent()) {
            User user = optional.get();
            if (validatePassword(user, password)) {

                String session = userDao.startSession(user, rm);
                user.setSessionId(session);

                return Optional.of(user);
            } else {
                return Optional.absent();
            }
        } else {
            return Optional.absent();
        }
    }

    public User updatePassword(User u, String password) {

        return userDao.updatePassword(u, password);


    }

    void setResetPasswordToken(User user, String token) {

        userDao.setResetPasswordToken(user, token);

    }

    protected EntityType getEntityType(HttpServletRequest req) {
        EntityType entityType = null;
        String type = req.getParameter(Parameters.type.getText());
        if (!StringUtils.isEmpty(type)) {
            Integer code = Integer.valueOf(type);
            entityType = EntityType.get(code);

        }
        if (entityType == null) {
            entityType = EntityType.point;
        }
        return entityType;
    }



}
