/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.transactions.user;

import com.google.appengine.api.users.UserServiceFactory;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.*;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKeyFactory;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModel;
import com.nimbits.cloudplatform.client.model.user.UserModelFactory;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.openid.UserInfo;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.settings.SettingsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class UserTransaction {
    private static final long serialVersionUID = 1L;

    public static final String AUTHENTICATED_KEY = "AUTHENTICATED_KEY";


    public static User getHttpRequestUser(final HttpServletRequest req) {


        String emailParam = null;
        HttpSession session = null;
        final com.google.appengine.api.users.UserService googleUserService = UserServiceFactory.getUserService();
        String accessKey = null;
        String uuid = null;
        UserInfo domainUser = null;
        if (req != null) {
            session = req.getSession();

            emailParam = req.getParameter(Parameters.email.getText());
            if (Utils.isEmptyString(emailParam)) {
                emailParam = req.getHeader(Parameters.email.getText());

            }
            accessKey = req.getParameter(Parameters.secret.getText());
            if (Utils.isEmptyString(accessKey)) {
                accessKey = req.getParameter(Parameters.key.getText());
            }
            if (Utils.isEmptyString(accessKey)) {
                accessKey = req.getHeader(Parameters.key.getText());

            }

            if (!Utils.isEmptyString(accessKey) && !Utils.isEmptyString(emailParam)) {
                String tempUserKey = MemCacheKey.userTempCacheKey + accessKey + emailParam;
                List<User> cached = UserCache.getCachedAuthenticatedUser(tempUserKey);
                if (!cached.isEmpty()) {
                    return cached.get(0);
                }
            }


            if (session != null) {
                domainUser = (UserInfo) req.getSession().getAttribute("user");
                if (domainUser != null) {

                    emailParam = domainUser.getEmail();

                }
            }

            uuid = req.getParameter(Parameters.uuid.getText());


        }

        EmailAddress email = Utils.isEmptyString(emailParam) ? null : CommonFactory.createEmailAddress(emailParam);

        if (email == null && session != null && session.getAttribute(Parameters.email.getText()) != null) {
            email = (EmailAddress) session.getAttribute(Parameters.email.getText());
        }

        try {
            if (googleUserService != null) {
                if (email == null && googleUserService.getCurrentUser() != null) {
                    email = CommonFactory.createEmailAddress(googleUserService.getCurrentUser().getEmail());
                }
            }
        } catch (NullPointerException e) {
            LogHelper.logException(UserTransaction.class, e);
            email = null;
        }


        if (email == null && Utils.isEmptyString(uuid)) {
            throw new SecurityException("There was no account connected to this request and no key or uuid, nothing to do.");
        }

        boolean anonRequest = false;

        if (!Utils.isEmptyString(uuid) && email == null) { //a request with just a uuid must be public
            List<Entity> anon = EntityServiceImpl.findEntityByKey(getAnonUser(), uuid);
            anonRequest = true;
            if (!anon.isEmpty()) {

                Entity anonEntity = anon.get(0);
                if (anonEntity.getProtectionLevel().equals(ProtectionLevel.everyone)) {

                    email = CommonFactory.createEmailAddress(anon.get(0).getOwner());
                } else {
                    throw new SecurityException("The object you requested was found, but its protection level was set to high to access. Try " +
                            "adding an email address and access key to your request.");
                }

            }
        }

        User user = null;
        if (email != null) {

            final List<Entity> result = EntityServiceImpl.getEntityByKey(
                    getAdmin(), //avoid infinite recursion
                    email.getValue(), EntityType.user);


            if (result.isEmpty()) {
                if (googleUserService != null) {
                    if (googleUserService.getCurrentUser() != null && googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(email.getValue())) {
                        user = createUserRecord(email);
                        user.addAccessKey(authenticatedKey(user));
                    } else if (domainUser != null) {
                        user = createUserRecord(email);
                        user.addAccessKey(authenticatedKey(user));
                    } else if (googleUserService.getCurrentUser() != null && !googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(email.getValue())) {
                        throw new SecurityException("While the current user is authenticated, the email provided does not match " +
                                "the authenticated user, so the system is confused and cannot authenticate the request. " +
                                "Please report this error.");
                    } else if (googleUserService.getCurrentUser() == null) {
                        throw new SecurityException(email.getValue() + " was provided but could not be found. Please log into nimbits with an account " +
                                " registered with google at least once.");
                    }
                }


            } else {
                user = (User) result.get(0);
                //
                if (!anonRequest) {
                    if (!Utils.isEmptyString(accessKey)) {
                        //all we have is an email of an existing user, let's see what they can do.
                        final Map<String, Entity> keys = EntityServiceImpl.getEntityModelMap(user, EntityType.accessKey, 1000);
                        for (final Entity k : keys.values()) {
                            if (((AccessKey) k).getCode().equals(accessKey)) {
                                user.addAccessKey((AccessKey) k);
                            }
                        }


                    }
                }
                if (user.isRestricted()) {

                    if (googleUserService != null) {
                        if (googleUserService.getCurrentUser() != null
                                && googleUserService.getCurrentUser().getEmail().equalsIgnoreCase(user.getEmail().getValue())) {

                            user.addAccessKey(authenticatedKey(user)); //they are logged in
                        }
                    }
                }
                if (domainUser != null) {
                    user.addAccessKey(authenticatedKey(user)); //they are logged in from google apps
                }

            }
        } else {
            throw new IllegalArgumentException("There was no account connected to this request");
        }

        return user;

    }


    public static AccessKey authenticatedKey(final Entity user) {

        final EntityName name = CommonFactory.createName(AUTHENTICATED_KEY, EntityType.accessKey);
        final Entity en = EntityModelFactory.createEntity(name, "", EntityType.accessKey, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        return AccessKeyFactory.createAccessKey(en, AUTHENTICATED_KEY, user.getKey(), AuthLevel.admin);
    }


    public static User createUserRecord(final EmailAddress internetAddress) {
        final EntityName name = CommonFactory.createName(internetAddress.getValue(), EntityType.user);
        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                name.getValue(), name.getValue(), name.getValue());

        final User newUser = UserModelFactory.createUserModel(entity);
        // newUser.setSecret(UUID.randomUUID().toString());


        return (User) EntityServiceImpl.addUpdateEntity(Arrays.<Entity>asList(newUser)).get(0);


    }


    public static User getAdmin()   {
        final String adminStr = SettingsServiceImpl.getSetting(SettingType.admin.getName());
        if (Utils.isEmptyString(adminStr)) {
            throw new IllegalArgumentException("Server is missing admin setting!");
        } else {
            final User u = new UserModel();
            u.setName(CommonFactory.createName(adminStr, EntityType.user));
            u.setKey(adminStr);
            u.addAccessKey(createAccessKey(u, AuthLevel.admin));
            u.setParent(adminStr);
            u.setUserAdmin(true);

            return u;
        }
    }

    private static AccessKey createAccessKey(final Entity u, final AuthLevel authLevel) {

        final Entity en = EntityModelFactory.createEntity(u.getName(), "", EntityType.accessKey, ProtectionLevel.onlyMe,
                u.getName().getValue(), u.getName().getValue());
        return AccessKeyFactory.createAccessKey(en, u.getName().getValue(), u.getName().getValue(), authLevel);

    }


    public static User getAnonUser() {
        User u = new UserModel();
        try {
            String adminStr = Const.CONST_ANON_EMAIL;
            u.setName(CommonFactory.createName(adminStr, EntityType.user));
        } catch (Exception e) {
            return u;
        }


        return u;
    }


    protected static User getAppUserUsingGoogleAuth() throws Exception {
        final com.google.appengine.api.users.UserService u = UserServiceFactory.getUserService();
        //u.getCurrentUser().

        User retObj = null;
        if (u.getCurrentUser() != null) {
            final EmailAddress emailAddress = CommonFactory.createEmailAddress(u.getCurrentUser().getEmail());
            List<Entity> result = EntityServiceImpl.getEntityByKey(getAnonUser(), emailAddress.getValue(), EntityType.user);
            if (!result.isEmpty()) {
                retObj = (User) result.get(0);
            }

        }

        return retObj;
    }


    public static List<User> getUserByKey(final String key, AuthLevel authLevel)  {
        List<Entity> result = EntityServiceImpl.getEntityByKey(getAnonUser(), key, EntityType.user);
        if (result.isEmpty()) {
            return Collections.emptyList();
        } else {
            User retObj = (User) result.get(0);
            AccessKey k = createAccessKey(retObj, authLevel);
            retObj.addAccessKey(k);

            return Arrays.asList(retObj);
        }

    }





}
