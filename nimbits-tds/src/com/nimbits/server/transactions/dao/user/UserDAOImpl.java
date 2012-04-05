/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.dao.user;

import com.nimbits.PMF;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.server.connections.ConnectionRequestModelFactory;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.orm.*;
import com.nimbits.server.orm.ConnectionRequestEntity;
import com.nimbits.server.user.UserTransactions;
import twitter4j.auth.AccessToken;

import javax.jdo.*;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class UserDAOImpl implements UserTransactions {
    private static final Logger log = Logger.getLogger(UserDAOImpl.class.getName());




    public User setFacebookToken(final EmailAddress internetAddress, final String token, final long facebookId) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        User retObj;
        try {

            final User u = getUserByKey(pm, internetAddress.getValue());
            if (u != null) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();

                u.setFacebookToken(token);
                u.setFacebookID(facebookId);
                tx.commit();

            }
            retObj = UserModelFactory.createUserModel(u);

        } finally {
            pm.close();
        }

        return retObj;
    }


    public User createNimbitsUser(final EmailAddress internetAddress) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final EntityName name = CommonFactoryLocator.getInstance().createName(internetAddress.getValue(), EntityType.user);
            final Entity entity =  EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                    "","");


            final Entity r = EntityTransactionFactory.getDaoInstance(null).addUpdateEntity(entity);

            final UserEntity u = new UserEntity(r, internetAddress);


            u.setSecret(UUID.randomUUID().toString());
            pm.makePersistent(u);




            return  UserModelFactory.createUserModel(u);

        } finally {
            pm.close();
        }


    }


    /* (non-Javadoc)
    * @see com.nimbits.server.user.UserTransactions#getNimbitsUser(java.lang.String, boolean, java.lang.String)
    */
    @Override
    public User getNimbitsUser(final EmailAddress internetAddress) {
        User retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            if (internetAddress != null) {

                final User u = getUserByKey(pm, internetAddress.getValue());
                if (u != null) {

                    retObj = UserModelFactory.createUserModel(u);
                }

            }

        } finally {
            pm.close();
        }
        return retObj;
    }


    /* (non-Javadoc)
      * @see com.nimbits.server.user.UserTransactions#getNimbitsUserByID(long)
      */

    /* (non-Javadoc)
    * @see com.nimbits.server.user.UserTransactions#updateSecret()
    */
    @Override
    public User updateSecret(final EmailAddress emailAddress, final UUID uuid) {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        User retObj = null;


        try {
            final User u = getUserByKey(pm, emailAddress.getValue());
            if (u != null) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();

                u.setSecret(uuid.toString());
                retObj = UserModelFactory.createUserModel(u);
                tx.commit();

            }
        } finally {
            pm.close();
        }
        return retObj;


    }


    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> getAllUsers(final String ordering, final int count) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<User> result;
        List<User> retObj = null;
        try {
            final Query q = pm.newQuery(UserEntity.class);
            q.setOrdering(ordering);
            q.setRange(0, count);
            result = (List<User>) q.execute();
            retObj = UserModelFactory.createUserModels(result);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }
        return retObj;
    }

    @Override
    public List<User> getUsers() {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<User> result;
        List<User> retObj = null;
        try {
            final Query q = pm.newQuery(UserEntity.class);
            result = (List<User>) q.execute();
            retObj = UserModelFactory.createUserModels(result);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }
        return retObj;
    }
    @Override
    public List<Connection> getPendingConnectionRequests(final EmailAddress internetAddress) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(ConnectionRequestEntity.class);
            q.setFilter("approved == a && targetEmail==e && rejected == r");
            q.declareParameters("Boolean a, Boolean r, String e");
            q.setRange(0,25);


            final List<ConnectionRequestEntity> data = (List<ConnectionRequestEntity>) q.execute(false, false, internetAddress.getValue());

            return  ConnectionRequestModelFactory.CreateConnectionRequestModels(data);

        } finally {
            pm.close();
        }


    }

    @Override
    public List<User> updateConnectionRequest(final Long key, final User requestor, final User acceptor, final boolean accepted) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<User> affectedUsers = new ArrayList<User>(1);

        final Transaction tx;

        try {
            final ConnectionRequestEntity c =  pm.getObjectById(ConnectionRequestEntity.class, key);
            if (c!= null) {
                tx = pm.currentTransaction();
                tx.begin();
                c.setRejected(!accepted);
                c.setApproved(accepted);
                c.setApprovedDate(new Date());
                tx.commit();
            }
        } finally {
            pm.close();
        }



        return affectedUsers;

    }



    @Override
    public Connection makeConnectionRequest(final User u, final EmailAddress emailAddress) {
        final ConnectionRequestEntity f = new ConnectionRequestEntity(u.getKey(), u.getEmail(), emailAddress, UUID.randomUUID().toString());
        Connection retObj;

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(f);
            retObj = ConnectionRequestModelFactory.CreateConnectionRequestModel(f);

        } finally {
            pm.close();
        }
        return retObj;


    }

    @Override
    public User updateTwitter(final EmailAddress internetAddress, final AccessToken token) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        User retObj = null;
         try {

             final User u = getUserByKey(pm, internetAddress.getValue());
            if (u != null) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();

                u.setTwitterToken(token.getToken());
                u.setTwitterTokenSecret(token.getTokenSecret());
                tx.commit();
                retObj = UserModelFactory.createUserModel(u);

            }

        } finally {
            pm.close();
        }


        return retObj;
    }

    @Override
    public User updateLastLoggedIn(final User user, final Date lastLoggedIn) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final User retObj;
        try {
            final Transaction tx = pm.currentTransaction();
            final UserEntity n = pm.getObjectById(UserEntity.class, user.getKey());

            tx.begin();
            n.setLastLoggedIn(lastLoggedIn);
            tx.commit();
            retObj = UserModelFactory.createUserModel(n);
            return retObj;
        } catch (ConcurrentModificationException ex) {
            log.severe(ex.getMessage());
            return user;
        } finally {
            pm.close();
        }


    }

    @Override
    public User getUserByKey(final String key) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final User user = getUserByKey(pm, key);
            if (user != null) {
            return UserModelFactory.createUserModel(user);
            }
            else {
                return null;
            }

        }catch (JDOObjectNotFoundException ex) {
            return null;
        } finally {
            pm.close();
        }



    }

    private static User getUserByKey(final PersistenceManager pm, final String key) {

        try {
            return pm.getObjectById(UserEntity.class, key);
        }catch (JDOObjectNotFoundException ex) {
            return null;
        }

    }


    @Override
    public List<User> getConnectionRequests(final List<String> connections) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequestEntity.class,":p.contains(uuid)");


        try {
            User u;
            final List<ConnectionRequestEntity> result = (List<ConnectionRequestEntity>) q.execute(connections);
            final List<User> retObj = new ArrayList<User>(result.size());
            if (result.size() > 0) {

                for (final Connection c : result) {
                    u = getNimbitsUser(c.getTargetEmail());
                    retObj.add( UserModelFactory.createUserModel(u));

                }


            }
            return retObj;
        } finally {
            pm.close();
        }

    }


}
