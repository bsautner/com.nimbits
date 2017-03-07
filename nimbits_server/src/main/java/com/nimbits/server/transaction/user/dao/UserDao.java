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

package com.nimbits.server.transaction.user.dao;


import com.google.common.base.Optional;
import com.nimbits.client.model.user.User;
import com.nimbits.server.PMF;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.transaction.entity.EntityHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;


@Repository
public class UserDao {
    private final PersistenceManagerFactory persistenceManagerFactory;

    @Autowired
    public UserDao(PMF pmf) {
       // SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.persistenceManagerFactory = pmf.get();
    }

    public void setResetPasswordToken(User user, String token) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {


            final Transaction tx = pm.currentTransaction();

            tx.begin();

            User attachedUser = pm.getObjectById(UserEntity.class, user.getId());
            attachedUser.setPasswordResetToken(token);
            attachedUser.setPasswordResetTimestamp(System.currentTimeMillis());
            tx.commit();

        } finally {
            pm.close();
        }

    }

    public User updatePassword(User user, String password) {
        String passwordSalt = RandomStringUtils.randomAscii(20);

        String cryptPassword = DigestUtils.sha512Hex(password + passwordSalt);
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {


            final Transaction tx = pm.currentTransaction();

            tx.begin();

            User attachedUser = pm.getObjectById(UserEntity.class, user.getId());
            attachedUser.setPasswordResetToken("");
            attachedUser.setPasswordResetTimestamp(0L);
            attachedUser.setPassword(cryptPassword);
            attachedUser.setPasswordSalt(passwordSalt);

            tx.commit();
            return (User) EntityHelper.createModel(user, attachedUser);

        } finally {
            pm.close();
        }

    }

    public Optional<User> getUserByEmail(String email) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;

            q1 = pm.newQuery(UserEntity.class);


            q1.setFilter("owner==b");
            q1.declareParameters("String b");

            final List<User> result = (List<User>) q1.execute(email);
            if (result.isEmpty()) {
                return Optional.absent();
            } else {
                User user = result.get(0);

                return Optional.of((User) EntityHelper.createModel(user, user));

            }
        } catch (Exception ex) {
            return Optional.absent();


        } finally {
            pm.close();
        }
    }

    public Optional<User> getUserById(String id) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;

            q1 = pm.newQuery(UserEntity.class);


            q1.setFilter("id==b");
            q1.declareParameters("String b");

            final List<User> result = (List<User>) q1.execute(id);
            if (result.isEmpty()) {
                return Optional.absent();
            } else {
                User user = result.get(0);

                return Optional.of((User) EntityHelper.createModel(user, user));

            }
        } catch (Exception ex) {
            return Optional.absent();


        } finally {
            pm.close();
        }
    }


    public boolean usersExist() {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        try {

            final Query q1 = pm.newQuery(UserEntity.class);


            q1.setRange(0, 1);
            final List<UserEntity> c = (List<UserEntity>) q1.execute();
            return !c.isEmpty();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);

        } finally {
            pm.close();
        }
    }


    public User getAdmin() {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        try {

            final Query q1 = pm.newQuery(UserEntity.class);
            q1.setFilter("isAdmin==b");
            q1.declareParameters("Boolean b");


            q1.setRange(0, 1);
            final List<UserEntity> c = (List<UserEntity>) q1.execute(true);
            if (c.isEmpty()) {
                throw new RuntimeException("Missing System Admin User");
            }
            else {
                return pm.detachCopy(c.get(0));
            }
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);

        } finally {
            pm.close();
        }
    }
}
