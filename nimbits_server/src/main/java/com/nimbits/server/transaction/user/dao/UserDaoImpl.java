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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.orm.SessionStore;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.transaction.entity.EntityHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public class UserDaoImpl implements UserDao {
    private PersistenceManagerFactory persistenceManagerFactory;
    private final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class.getName());

    public UserDaoImpl() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

    }

    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }



    @Override
    public void setResetPasswordToken(User user, String token) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {


            final Transaction tx = pm.currentTransaction();

            tx.begin();

            User attachedUser = pm.getObjectById(UserEntity.class, user.getId());
            attachedUser.setPasswordResetToken(token);
            attachedUser.setPasswordResetTimestamp(System.currentTimeMillis());

            logger.info("commited update: setResetPasswordToken " + user.toString());
            tx.commit();

        } finally {
            pm.close();
        }

    }

    @Override
    public User updatePassword(User u, String password) {
        String passwordSalt = RandomStringUtils.randomAscii(20);

        String cryptPassword = DigestUtils.sha512Hex(password + passwordSalt);
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {


            final Transaction tx = pm.currentTransaction();

            tx.begin();

            User attachedUser = pm.getObjectById(UserEntity.class, u.getId());
            attachedUser.setPasswordResetToken("");
            attachedUser.setPasswordResetTimestamp(0L);
            attachedUser.setPassword(cryptPassword);
            attachedUser.setPasswordSalt(passwordSalt);
            logger.info("commited update: updatePassword " + attachedUser.toString());

            tx.commit();
            return (User) EntityHelper.createModel(u, attachedUser);

        } finally {
            pm.close();
        }

    }

    @Override
    public void storeAuthToken(String email, String session) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {


            final Transaction tx = pm.currentTransaction();

            tx.begin();

            SessionStore sessionStore = new SessionStore(email, session);
            pm.makePersistent(sessionStore);

            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();


        } finally {
            pm.close();
        }
    }

    @Override
    public void deleteAuthToken(String session) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;

            q1 = pm.newQuery(SessionStore.class);


            q1.setFilter("session==b");
            q1.declareParameters("String b");

            final Collection<SessionStore> result = (Collection<SessionStore>) q1.execute(session);
            pm.deletePersistentAll(result);


        } finally {
            pm.close();
        }
    }

    @Override
    public User getUserByAuthToken(String session) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;

            q1 = pm.newQuery(SessionStore.class);


            q1.setFilter("session==b");
            q1.declareParameters("String b");

            final List<SessionStore> result = (List<SessionStore>) q1.execute(session);
            if (result.isEmpty()) {
                throw new SecurityException("Session Not Found");
            } else {
                SessionStore sessionStore = result.get(0);
                User user = pm.getObjectById(UserEntity.class, sessionStore.getEmail());
                return (User) EntityHelper.createModel(user, user);

            }
        } catch (Exception ex) {
            deleteAuthToken(session);
            throw new SecurityException("Bad Session");


        } finally {
            pm.close();
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;

            q1 = pm.newQuery(UserEntity.class);


            q1.setFilter("owner==b");
            q1.declareParameters("String b");

            final List<User> result = (List<User>) q1.execute(email);
            if (result.isEmpty()) {
                throw new SecurityException("User Not Found");
            } else {
                User user =  result.get(0);

                return Optional.of((User) EntityHelper.createModel(user, user));

            }
        } catch (Exception ex) {
            return Optional.absent();


        } finally {
            pm.close();
        }
    }



    @Override
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

    @Override
    public boolean userHasPoints(User user) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Collection<String> ownerKeys = new ArrayList<String>(1);
            ownerKeys.add(user.getId());

            final Query q1;

            Collection<Entity> result;
            try {
                q1 = pm.newQuery(Class.forName(EntityType.point.getClassName()), ":p.contains(owner)");
                q1.setRange(0, 1);
                result = (Collection<Entity>) q1.execute(ownerKeys);
            } catch (ClassNotFoundException e) {
                result = Collections.emptyList();
            }
            return !result.isEmpty();


        } finally {
            pm.close();
        }

    }


}
