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

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.orm.SessionStore;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.orm.socket.SocketStore;
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

@Repository
public class UserDaoImpl implements UserDao {
    private PersistenceManagerFactory persistenceManagerFactory;


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

            User attachedUser = pm.getObjectById(UserEntity.class, user.getKey());
            attachedUser.setPasswordResetToken(token);
            attachedUser.setPasswordResetTokenTimestamp(new Date());


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

            User attachedUser = pm.getObjectById(UserEntity.class, u.getKey());
            attachedUser.setPasswordResetToken("");
            attachedUser.setPasswordResetTokenTimestamp(new Date(0));
            attachedUser.setPassword(cryptPassword);
            attachedUser.setPasswordSalt(passwordSalt);


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
    public User getUserByEmail(String email) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {


            User user = pm.getObjectById(UserEntity.class, email);
            return (User) EntityHelper.createModel(user, user);


        } finally {
            pm.close();
        }
    }

    @Override
    public void startSocketSession(User user) {


        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {


            final Transaction tx = pm.currentTransaction();

            tx.begin();

            final SocketStore socketStore = new SocketStore(user);
            pm.makePersistent(socketStore);

            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();


        } finally {
            pm.close();
        }

    }

    //TODO delete old sessions, only get one per socket - add more info like socket id to make this more granular

    @Override
    public List<SocketStore> getSocketSessions(User user) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;
            q1 = pm.newQuery(SocketStore.class);
            q1.setFilter("email==b");
            q1.declareParameters("String b");
            final List<SocketStore> result = (List<SocketStore>) q1.execute(user.getEmail().getValue());
            return result;
        } finally {
            pm.close();
        }
    }

    @Override
    public List<User> getAllUsers() {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        List<User> retObj = new ArrayList<>();

        try {

            final Query q1 = pm.newQuery(UserEntity.class);



            final List<UserEntity> c = (List<UserEntity>) q1.execute();
            for (UserEntity u : c) {
                retObj.add(new UserModel.Builder().init(u).create());
            }
            return retObj;

        } finally {
            pm.close();
        }


    }

    @Override
    public User getUserByIndex(int index) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        try {

            final Query q1 = pm.newQuery(UserEntity.class);

             q1.setRange(index, index + 1);
            final List<UserEntity> c = (List<UserEntity>) q1.execute();
            return new UserModel.Builder().init(c.get(0)).create();
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
        } finally {
            pm.close();
        }
    }

    @Override
    public boolean userHasPoints(User user) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Collection<String> ownerKeys = new ArrayList<String>(1);
            ownerKeys.add(user.getKey());

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
