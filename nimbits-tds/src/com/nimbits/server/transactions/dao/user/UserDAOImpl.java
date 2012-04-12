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

import com.nimbits.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.user.*;

import javax.jdo.*;
import java.util.*;
import java.util.logging.*;

@SuppressWarnings("unchecked")
public class UserDAOImpl implements UserTransactions {
    private static final Logger log = Logger.getLogger(UserDAOImpl.class.getName());
    private static final int MAX_REQUESTS = 25;

//
//    @Override
//    public User setFacebookToken(final EmailAddress internetAddress, final String token, final long facebookId) throws NimbitsException {
//
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        try {
//
//            final User u = getUserByKey(pm, internetAddress.getValue());
//            if (u != null) {
//                final Transaction tx = pm.currentTransaction();
//                tx.begin();
//
//                u.setFacebookToken(token);
//                u.setFacebookID(facebookId);
//                tx.commit();
//                return  UserModelFactory.createUserModel(u);
//            }
//            else {
//                throw new NimbitsException("User Not Found");
//            }
//
//
//        } finally {
//            pm.close();
//        }
//
//
//    }



    /* (non-Javadoc)
    * @see com.nimbits.server.user.UserTransactions#getNimbitsUser(java.lang.String, boolean, java.lang.String)
    */
//    @Override
//    public User getNimbitsUser(final EmailAddress internetAddress) throws NimbitsException {
//        User retObj = null;
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        try {
//            if (internetAddress != null) {
//
//                final User u = getUserByKey(pm, internetAddress.getValue());
//                if (u != null) {
//
//                    retObj = UserModelFactory.createUserModel(u);
//                }
//
//            }
//        } catch (Exception ex) {
//            if (internetAddress != null) {
//                log.info(internetAddress.getValue());
//            }
//            LogHelper.logException(this.getClass(), ex);
//        }
//        finally {
//            pm.close();
//        }
//        return retObj;
//    }


    /* (non-Javadoc)
      * @see com.nimbits.server.user.UserTransactions#getNimbitsUserByID(long)
      */

    /* (non-Javadoc)
    * @see com.nimbits.server.user.UserTransactions#updateSecret()
    */
//    @Override
//    public User updateSecret(final EmailAddress emailAddress, final UUID uuid) throws NimbitsException {
//
//
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        User retObj = null;
//
//
//        try {
//            final User u = getUserByKey(pm, emailAddress.getValue());
//            if (u != null) {
//                final Transaction tx = pm.currentTransaction();
//                tx.begin();
//
//                u.setSecret(uuid.toString());
//                retObj = UserModelFactory.createUserModel(u);
//                tx.commit();
//
//            }
//        } finally {
//            pm.close();
//        }
//        return retObj;
//
//
//    }


    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> getAllUsers(final String ordering, final int count) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<User> retObj = null;
        try {
            final Query q = pm.newQuery(UserEntity.class);
            q.setOrdering(ordering);
            q.setRange(0, count);
            final Collection<User> result = (Collection<User>) q.execute();
            retObj = UserModelFactory.createUserModels(result);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }
        return retObj;
    }

//    @Override
//    public List<User> getUsers() throws NimbitsException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        try {
//            final Query q = pm.newQuery(UserEntity.class);
//            final Collection<User> result = (Collection<User>) q.execute();
//            return  UserModelFactory.createUserModels(result);
//        }  finally {
//            pm.close();
//        }
//
//    }
    @Override
    public List<ConnectionRequest> getPendingConnectionRequests(final EmailAddress internetAddress) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(ConnectionRequestEntity.class);
            q.setFilter("approved == a && targetEmail==e && rejected == r");
            q.declareParameters("Boolean a, Boolean r, String e");
            q.setRange(0, MAX_REQUESTS);


            final List<ConnectionRequestEntity> data = (List<ConnectionRequestEntity>) q.execute(false, false, internetAddress.getValue());

            return  ConnectionRequestModelFactory.CreateConnectionRequestModels(data);

        } finally {
            pm.close();
        }


    }

    @Override
    public void updateConnectionRequest(final Long key, final User requestor, final User acceptor, final boolean accepted) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        //nal List<User> affectedUsers = new ArrayList<User>(1);

        try {
            final ConnectionRequestEntity c =  pm.getObjectById(ConnectionRequestEntity.class, key);
            if (c!= null) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                c.setRejected(!accepted);
                c.setApproved(accepted);
                c.setApprovedDate(new Date());
                tx.commit();
            }
        } finally {
            pm.close();
        }



        // return affectedUsers;

    }



    @Override
    public ConnectionRequest makeConnectionRequest(final User u, final EmailAddress emailAddress) throws NimbitsException {
        final ConnectionRequestEntity f = new ConnectionRequestEntity(u.getKey(), u.getEmail(), emailAddress, UUID.randomUUID().toString());

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(f);
            return  ConnectionRequestModelFactory.CreateConnectionRequestModel(f);

        } finally {
            pm.close();
        }



    }
//
//    @Override
//    public User updateTwitter(final EmailAddress internetAddress, final AccessToken token) throws NimbitsException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        try {
//
//            final User u = getUserByKey(pm, internetAddress.getValue());
//            if (u != null) {
//                final Transaction tx = pm.currentTransaction();
//                tx.begin();
//
//                u.setTwitterToken(token.getToken());
//                u.setTwitterTokenSecret(token.getTokenSecret());
//                tx.commit();
//                return  UserModelFactory.createUserModel(u);
//
//            }
//            else {
//                throw new NimbitsException(UserMessages.ERROR_USER_NOT_FOUND);
//            }
//
//        } finally {
//            pm.close();
//        }
//
//
//    }

//    @Override
//    public User updateLastLoggedIn(final User user, final Date lastLoggedIn) throws NimbitsException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        try {
//            final Transaction tx = pm.currentTransaction();
//            final UserEntity n = pm.getObjectById(UserEntity.class, user.getKey());
//
//            tx.begin();
//            n.setLastLoggedIn(lastLoggedIn);
//            tx.commit();
//            return UserModelFactory.createUserModel(n);
//
//        } catch (ConcurrentModificationException ex) {
//            log.severe(ex.getMessage());
//            return user;
//        } finally {
//            pm.close();
//        }
//
//
//    }

//    @Override
//    public User getUserByKey(final String key) throws NimbitsException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        try {
//            final User user = getUserByKey(pm, key);
//            return user != null ? UserModelFactory.createUserModel(user) : null;
//
//        }catch (JDOObjectNotFoundException ex) {
//            return null;
//        } finally {
//            pm.close();
//        }
//
//
//
//    }

//    private static User getUserByKey(final PersistenceManager pm, final String key) {
//
//        try {
//            return pm.getObjectById(UserEntity.class, key);
//        }catch (JDOObjectNotFoundException ex) {
//            return null;
//        }
//
//    }


    @Override
    public List<User> getConnectionRequests(final List<String> connections) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequestEntity.class,":p.contains(uuid)");


        try {
            final Collection<ConnectionRequestEntity> result = (Collection<ConnectionRequestEntity>) q.execute(connections);
            final List<User> retObj = new ArrayList<User>(result.size());
            if (!result.isEmpty()) {

                for (final ConnectionRequest c : result) {
                    User u = (User) EntityServiceFactory.getInstance().getEntityByKey(c.getTargetEmail().getValue(), UserEntity.class.getName());
                    retObj.add( UserModelFactory.createUserModel(u));

                }


            }
            return retObj;
        } finally {
            pm.close();
        }

    }


}
