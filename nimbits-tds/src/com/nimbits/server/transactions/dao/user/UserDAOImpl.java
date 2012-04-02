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
import com.nimbits.server.orm.ConnectionRequest;
import com.nimbits.server.orm.UserEntity;
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
        UserEntity u = null;
        final Query q = pm.newQuery(UserEntity.class, "email == u");
        q.declareParameters("String u");
        q.setRange(0, 1);

        User retObj;
        try {
            final List<UserEntity> result = (List<UserEntity>) q.execute(internetAddress.getValue());
            //todo make sure unregistereed users from facebook get an account created.

            if (result.size() > 0) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                u = result.get(0);
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
                    "","", "");


            Entity r = EntityTransactionFactory.getDaoInstance(null).addUpdateEntity(entity);

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

                final Query q = pm.newQuery(UserEntity.class, "email == u");
                q.declareParameters("String u");
                q.setRange(0, 1);
                final List<UserEntity> result = (List<UserEntity>) q.execute(internetAddress.getValue());
                if (result.size() > 0) {
                    final UserEntity u = result.get(0);
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

        final Query q = pm.newQuery(UserEntity.class, "email == u");
        q.declareParameters("String u");
        try {
            final List<UserEntity> result = (List<UserEntity>) q.execute(emailAddress.getValue());
            if (result.size() > 0) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                final UserEntity n = result.get(0);
                n.setSecret(uuid.toString());
                retObj = UserModelFactory.createUserModel(n);
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
        final Query q = pm.newQuery(ConnectionRequest.class, "approved == a && targetEmail==e && rejected == r");
        final Map<String, Object> args = new HashMap<String, Object>(3);
        args.put("a", false);
        args.put("r", false);
        args.put("e", internetAddress.getValue());
        q.declareParameters("Boolean a, Boolean r, String e");
        final List<ConnectionRequest> data = (List<ConnectionRequest>) q.executeWithMap(args);

        final List<Connection> retObj = ConnectionRequestModelFactory.CreateConnectionRequestModels(data);
        pm.close();
        return retObj;

    }

    @Override
    public List<User> updateConnectionRequest(final String uuid, final User requestor, final User acceptor, final boolean accepted) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequest.class, "uuid == u");// && targetEmail==e && rejected != r");
        final List<User> affectedUsers = new ArrayList<User>(1);

        final Map<String, Object> args = new HashMap<String, Object>(1);
        args.put("u", uuid);

        q.declareParameters("String u");//, String e, Boolean r");

        q.setRange(0, 1);
        final List<ConnectionRequest> data = (List<ConnectionRequest>) q.executeWithMap(args);
        if (data.size() > 0) {
            final Transaction tx;
            tx = pm.currentTransaction();
            tx.begin();
            final ConnectionRequest c = data.get(0);
            c.setRejected(!accepted);
            c.setApproved(accepted);
            c.setApprovedDate(new Date());
            tx.commit();

        }
        pm.close();
        return affectedUsers;

    }



    @Override
    public Connection makeConnectionRequest(final User u, final EmailAddress emailAddress) {
        final ConnectionRequest f = new ConnectionRequest(u.getKey(), u.getEmail(), emailAddress, UUID.randomUUID().toString());
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
        final Query q = pm.newQuery(UserEntity.class, "email == u");
        User retObj = null;
        try {
            q.declareParameters("String u");
            final List<UserEntity> result = (List<UserEntity>) q.execute(internetAddress.getValue());
            if (result.size() > 0) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                final UserEntity n = result.get(0);
                n.setTwitterToken(token.getToken());
                n.setTwitterTokenSecret(token.getTokenSecret());
                retObj = UserModelFactory.createUserModel(n);
                tx.commit();
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


            return pm.getObjectById(UserEntity.class, key);
        }catch (JDOObjectNotFoundException ex) {


            return null;
        } finally {
            pm.close();
        }



    }

    @Override
    public List<User> getConnectionRequests(final List<String> connections) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequest.class,":p.contains(uuid)");


        try {
            User u;
            final List<ConnectionRequest> result = (List<ConnectionRequest>) q.execute(connections);
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
