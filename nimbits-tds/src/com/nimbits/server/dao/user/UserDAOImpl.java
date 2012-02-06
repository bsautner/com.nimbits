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

package com.nimbits.server.dao.user;

import com.nimbits.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.connections.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.pointcategory.*;
import com.nimbits.server.user.*;
import twitter4j.auth.*;

import javax.jdo.*;
import java.util.*;
import java.util.logging.*;


public class UserDAOImpl implements UserTransactions {
    private static final Logger log = Logger.getLogger(UserDAOImpl.class.getName());



    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public User setFacebookToken(final EmailAddress internetAddress, final String token, final long fbid) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        NimbitsUser u;
        Query q = pm.newQuery(NimbitsUser.class, "email == u");
        q.declareParameters("String u");
        q.setRange(0, 1);
        boolean isNew;
        User retObj;
        try {
            List<NimbitsUser> result = (List<NimbitsUser>) q.execute(internetAddress.getValue());
            isNew = false;

            if (result.size() > 0) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                u = result.get(0);
                u.setFacebookToken(token);
                u.setFacebookID(fbid);
                tx.commit();

            } else {
                isNew = true;
                u = new NimbitsUser(internetAddress, UUID
                        .randomUUID().toString());
                u.setHost("setFacebookToken");
                u.setUserSource("facebook");
                u.setSecret(UUID.randomUUID().toString());
                u.setSendEmail(true);
                u.setFacebookToken(token);
                u.setFacebookID(fbid);
                pm.makePersistent(u);

            }
            retObj = UserModelFactory.createUserModel(u);

        } finally {
            pm.close();
        }

        if (isNew) {
            CategoryServiceFactory.getInstance().createHiddenCategory(retObj);
        }
        return retObj;
    }


    public User createNimbitsUser(final EmailAddress internetAddress) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        User retObj = null;
        try {
            final NimbitsUser u = new NimbitsUser(internetAddress, UUID.randomUUID().toString());
            u.setSecret(UUID.randomUUID().toString());
            pm.makePersistent(u);
            CategoryServiceFactory.getInstance().createHiddenCategory(u);
            retObj = UserModelFactory.createUserModel(u);
        } finally {
            pm.close();
        }

        return retObj;

    }


    /* (non-Javadoc)
    * @see com.nimbits.server.user.UserTransactions#getNimbitsUser(java.lang.String, boolean, java.lang.String)
    */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public User getNimbitsUser(final EmailAddress internetAddress) {
        User retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            if (internetAddress != null) {

                final Query q = pm.newQuery(NimbitsUser.class, "email == u");
                q.declareParameters("String u");
                q.setRange(0, 1);
                final List<NimbitsUser> result = (List<NimbitsUser>) q.execute(internetAddress.getValue());
                if (result.size() > 0) {
                    final NimbitsUser u = result.get(0);
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
    @Override
    public User getNimbitsUserByID(final long id) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        User retObj;
        try {
            NimbitsUser u = pm.getObjectById(NimbitsUser.class, id);
            retObj = UserModelFactory.createUserModel(u);
        } finally {
            pm.close();
        }

        return retObj;


    }

    /* (non-Javadoc)
    * @see com.nimbits.server.user.UserTransactions#updateSecret()
    */
    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public User updateSecret(final EmailAddress emailAddress, final UUID uuid) {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        User retObj = null;

        final Query q = pm.newQuery(NimbitsUser.class, "email == u");
        q.declareParameters("String u");
        try {
            final List<NimbitsUser> result = (List<NimbitsUser>) q.execute(emailAddress.getValue());
            if (result.size() > 0) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                final NimbitsUser n = result.get(0);
                n.setSecret(uuid.toString());
                retObj = UserModelFactory.createUserModel(n);
                tx.commit();

            }
        } finally {
            pm.close();
        }
        return retObj;


    }

//    /* (non-Javadoc)
//    * @see com.nimbits.server.user.UserTransactions#doUserMaint(java.lang.String)
//    */
//    @Override
//    @SuppressWarnings(Const.WARNING_UNCHECKED)
//    public void doUserMaint(final String serverName) {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        List<NimbitsUser> a;
//        Transaction tx;
//        tx = pm.currentTransaction();
//
//        try {
//
////            Query q = pm.newQuery(NimbitsUser.class);
////            // q.setOrdering("LastChecked ascending");
////            a = (List<NimbitsUser>) q.execute();
////
////            for (User u : a) {
////                tx.begin();
////                if (u.getUUID() == null) {
////                    u.setUuid(UUID.randomUUID().toString());
////                }
////                if (u.getLastLoggedIn() == null) {
////                    u.setLastLoggedIn(u.getDateCreated());
////                }
////                // u.setHost(serverName);
////                // String e = u.getValue().toLowerCase();
////                // u.setEmail(e);
////                tx.commit();
////
////
////            }
//
//        } catch (Exception e) {
//            log.severe(e.getMessage());
//        } finally {
//
//            pm.close();
//
//        }
//    }

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> getAllUsers(final String ordering, int count) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<User> result;
        List<User> retObj = null;
        try {
            Query q = pm.newQuery(NimbitsUser.class);
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
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> getUsers(int start, int end) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<User> result;
        List<User> retObj = null;
        try {
            Query q = pm.newQuery(NimbitsUser.class);

            q.setRange(start, end);
            result = (List<User>) q.execute();
            retObj = UserModelFactory.createUserModels(result);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }
        return retObj;
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<Connection> getPendingConnectionRequests(final EmailAddress internetAddress) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequest.class, "approved == a && targetEmail==e && rejected == r");
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put("a", false);
        args.put("r", false);
        args.put("e", internetAddress.getValue());
        q.declareParameters("Boolean a, Boolean r, String e");
        List<ConnectionRequest> data = (List<ConnectionRequest>) q.executeWithMap(args);

        List<Connection> retObj = ConnectionRequestModelFactory.CreateConnectionRequestModels(data);
        pm.close();
        return retObj;

    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> updateConnectionRequest(final String uuid, final User requestor, final User acceptor, final boolean accepted) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequest.class, "uuid == u");// && targetEmail==e && rejected != r");
        final List<User> affectedUsers = new ArrayList<User>();

        final Map<String, Object> args = new HashMap<String, Object>();
        args.put("u", uuid);

        q.declareParameters("String u");//, String e, Boolean r");

        q.setRange(0, 1);
        List<ConnectionRequest> data = (List<ConnectionRequest>) q.executeWithMap(args);
        if (data.size() > 0) {
            Transaction tx;
            tx = pm.currentTransaction();
            tx.begin();
            ConnectionRequest c = data.get(0);
            c.setRejected(!accepted);
            c.setApproved(accepted);
            c.setApprovedDate(new Date());
            tx.commit();
            if (accepted) {
                NimbitsUser r = pm.getObjectById(NimbitsUser.class, requestor.getId());

                if (r != null) {
                    Transaction txr = pm.currentTransaction();
                    txr.begin();
                    r.addConnection(acceptor.getId());
                    txr.commit();
                    affectedUsers.add(UserModelFactory.createUserModel(r));
                }
                NimbitsUser a = pm.getObjectById(NimbitsUser.class, acceptor.getId());

                if (a != null) {
                    Transaction txr = pm.currentTransaction();
                    txr.begin();
                    a.addConnection(requestor.getId());
                    txr.commit();
                    affectedUsers.add(UserModelFactory.createUserModel(a));
                }
            }
        }
        pm.close();
        return affectedUsers;

    }

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> getConnections(final EmailAddress internetAddress) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final LinkedList<User> users = new LinkedList<User>();
        final Query q = pm.newQuery(NimbitsUser.class, "email == e");
        final Map<String, Object> args = new HashMap<String, Object>();

        try {
            args.put("e", internetAddress.getValue());
            q.declareParameters("String e");
            List<NimbitsUser> data = (List<NimbitsUser>) q.executeWithMap(args);
            if (data.size() > 0) {

                NimbitsUser n = data.get(0);
                NimbitsUser a;
                if (n.getConnections() != null && n.getConnections().size() > 0) {
                    for (Long id : n.getConnections()) {
                        try {
                            a = pm.getObjectById(NimbitsUser.class, id);
                        } catch (Exception ex) {
                            a = null;
                        }

                        if (a != null) {
                            users.add(UserModelFactory.createUserModel(a));

                        }
                    }
                }
            }
        } finally {
            pm.close();
        }


        return users;
    }


    public Connection makeConnectionRequest(final User u, final EmailAddress emailAddress) {
        final ConnectionRequest f = new ConnectionRequest(u.getId(), u.getEmail(), emailAddress, UUID.randomUUID().toString());
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

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public User updateTwitter(final EmailAddress internetAddress, final AccessToken token) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(NimbitsUser.class, "email == u");
        User retObj = null;
        try {
            q.declareParameters("String u");
            final List<NimbitsUser> result = (List<NimbitsUser>) q.execute(internetAddress.getValue());
            if (result.size() > 0) {
                Transaction tx = pm.currentTransaction();
                tx.begin();
                NimbitsUser n = result.get(0);
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
            Transaction tx = pm.currentTransaction();
            NimbitsUser n = pm.getObjectById(NimbitsUser.class, user.getId());

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
    public User getUserByUUID(String subscriberUUID) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(NimbitsUser.class, "uuid == u");
        User retObj = null;
        try {
            q.declareParameters("String u");
            final List<NimbitsUser> result = (List<NimbitsUser>) q.execute(subscriberUUID);
            if (result.size() > 0) {

                NimbitsUser n = result.get(0);
                retObj = UserModelFactory.createUserModel(n);

            }
            return retObj;
        } finally {
            pm.close();
        }



    }


}
