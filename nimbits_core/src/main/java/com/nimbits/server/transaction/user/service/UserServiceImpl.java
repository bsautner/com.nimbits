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

package com.nimbits.server.transaction.user.service;

import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingServiceFactory;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.transaction.user.cache.UserCache;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class UserServiceImpl implements UserService {
    public static final String ERROR1 = "Could not authenticate your request.";
    private final EntityService entityService;
    public static final String AUTHENTICATED_KEY = "AUTHENTICATED_KEY";
    public static final int LIMIT = 1000;
    protected EmailAddress email;
    private final UserCache userCache;

    private final NimbitsEngine engine;
    @Override
    public EmailAddress getEmail() {
        return email;
    }

    public UserServiceImpl(NimbitsEngine engine) {

        this.userCache = AuthenticationServiceFactory.getCacheInstance(engine);
        this.entityService = EntityServiceFactory.getInstance(engine);
        this.engine = engine;
    }

    static {

    }

    @Override
    public User getHttpRequestUser(final HttpServletRequest req) {


        getEmailFromRequest(req);

        getEmailFromLoggedInUser();

        processAnonRequestWithUUID(req);




        if (email != null) {
            User user;
            final List<Entity> result = entityService.getEntityByKey(
                    getAdmin(), //avoid infinite recursion
                    email.getValue(), EntityType.user);


            if (result.isEmpty()) {


                if (trustedRequest(req)) {
                    user = createUserRecord(email);
                    user.addAccessKey(authenticatedKey(user));

                    return user;

                } else {
                    throw new SecurityException(ERROR1);
                }




            } else {
                 user = (User) result.get(0);

                addAccessKeysToUser(req,  user);

                if ( user.isRestricted() && trustedRequest(req)) {

                     user.addAccessKey(authenticatedKey(user)); //they are logged in
                }


            }

            return  user;
        }
        else {
            throw new SecurityException("There was no account connected to this request");
        }



    }

    private boolean trustedRequest(final HttpServletRequest request) {
        List<EmailAddress> emailSample = engine.getAuthenticationMechanism().getCurrentUserEmail();
        EmailAddress emailAddress;
        if (emailSample.isEmpty()) {
            emailAddress = null;
        }
        else {
            emailAddress = emailSample.get(0);
        }
        return (emailAddress !=null && emailAddress.getValue().equalsIgnoreCase(email.getValue()))
                || validApiKey(request);
    }
    private boolean validApiKey(final HttpServletRequest request) {

        String apiKey = SettingServiceFactory.getServiceInstance(engine).getSetting(SettingType.apiKey);
        String providedApiKey = request.getHeader(Parameters.apikey.getText());
        if (StringUtils.isNotEmpty(apiKey) && StringUtils.isNotEmpty(providedApiKey)) {
            if (apiKey.equals(providedApiKey)) {
                return true;
            }

        }
        return false;

    }
    @Override
    public void getEmailFromRequest(HttpServletRequest req) {
        if (req != null) {
            String emailParam = req.getParameter(Parameters.email.getText());
            email = Utils.isEmptyString(emailParam) ? null : CommonFactory.createEmailAddress(emailParam);


        }
    }

    @Override
    public AccessKey authenticatedKey(final Entity authenticatedUser) {

        final EntityName name = CommonFactory.createName(AUTHENTICATED_KEY, EntityType.accessKey);
        final Entity en = EntityModelFactory.createEntity(name, "", EntityType.accessKey, ProtectionLevel.onlyMe,  authenticatedUser.getKey(),  authenticatedUser.getKey());
        return AccessKeyFactory.createAccessKey(en, AUTHENTICATED_KEY, authenticatedUser.getKey(), AuthLevel.admin);
    }

    @Override
    public User createUserRecord(final EmailAddress internetAddress) {
        final EntityName name = CommonFactory.createName(internetAddress.getValue(), EntityType.user);
        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                name.getValue(), name.getValue(), name.getValue());

        final User newUser = UserModelFactory.createUserModel(entity);
        // newUser.setSecret(UUID.randomUUID().toString());


        return (User) entityService.addUpdateEntity(Arrays.<Entity>asList(newUser)).get(0);


    }

    @Override
    public User getAdmin()   {
        final String adminStr = SettingServiceFactory.getServiceInstance(engine).getSetting(SettingType.admin);
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

    @Override
    public List<User> getUserByKey(final String key, AuthLevel authLevel)  {
        List<Entity> result = entityService.getEntityByKey(getAnonUser(), key, EntityType.user);
        if (result.isEmpty()) {
            return Collections.emptyList();
        } else {
            User retObj = (User) result.get(0);
            AccessKey k = createAccessKey(retObj, authLevel);
            retObj.addAccessKey(k);

            return Arrays.asList(retObj);
        }

    }

    @Override
    public User getAnonUser() {
        User u = new UserModel();
        try {

            u.setName(CommonFactory.createName(Const.CONST_ANON_EMAIL, EntityType.user));
        } catch (Exception e) {
            return u;
        }


        return u;
    }




    protected void addAccessKeysToUser(HttpServletRequest req, User user) {
        if (req != null && user != null) {
            String accessKey = getAccessKey(req);
            if (!Utils.isEmptyString(accessKey)) {
                //all we have is an email of an existing user, let's see what they can do.
                final Map<String, Entity> keys = entityService.getEntityModelMap(user, EntityType.accessKey, LIMIT);
                for (final Entity k : keys.values()) {
                    if (((AccessKey) k).getCode().equals(accessKey)) {
                        user.addAccessKey((AccessKey) k);
                    }
                }
            }
        }
    }

    protected List<User> getUserFromCache(String accessKey) {
        if (!Utils.isEmptyString(accessKey) && email != null) {

            return userCache.getCachedAuthenticatedUser(accessKey);

        }
        else {
            return Collections.emptyList();
        }
    }

    protected void getEmailFromLoggedInUser() {
        List<EmailAddress> emailSample = engine.getAuthenticationMechanism().getCurrentUserEmail();
        EmailAddress emailAddress;
        if (emailSample.isEmpty()) {
            emailAddress = null;
        }
        else {
            emailAddress = emailSample.get(0);
        }
        if (emailAddress != null) {
            if (this.email == null && emailAddress != null) {
                this.email = emailAddress;
            }
        }
    }

    protected String getAccessKey(HttpServletRequest req) {
        String accessKey = req.getParameter(Parameters.secret.getText());
        if (Utils.isEmptyString(accessKey)) {
            accessKey = req.getParameter(Parameters.key.getText());
        }

        return accessKey;
    }

//    private static void getEmailFromSession(HttpServletRequest req) {
//        if (email == null && req != null) {
//            HttpSession session = req.getSession();
//            if (email == null && session != null && session.getAttribute(Parameters.email.getText()) != null) {
//                email = (EmailAddress) session.getAttribute(Parameters.email.getText());
//            }
//        }
//    }
//    private static void getUserFromSession(HttpServletRequest req) {
//        if (req != null) {
//            HttpSession session = req.getSession();
//            if (session != null && session.getAttribute(Parameters.user.getText()) != null) {
//                user = (User) session.getAttribute(Parameters.user.getText());
//                user.setSessionId(session.getId());
//            }
//        }
//    }

//    private static void putUserInSession(HttpServletRequest req, User user) {
//        if (email == null && req != null) {
//            HttpSession session = req.getSession();
//            if (user != null && session != null) {
//                user.setSessionId(session.getId());
//                session.setAttribute(Parameters.user.getText(), user);
//                UserCache.cacheAuthenticatedUser(session.getId(), user);
//            }
//        }
//    }

    protected void processAnonRequestWithUUID(HttpServletRequest req) {

        if (req != null && this.email == null) {

            String uuid = req.getParameter(Parameters.uuid.getText());
            EntityType entityType = getEntityType(req);

            if (!Utils.isEmptyString(uuid) && this.email == null) { //a request with just a uuid must be public
                List<Entity> anon = entityService.getEntityByUUID(getAnonUser(), uuid, entityType);

                if (!anon.isEmpty()) {
                    Entity anonEntity = anon.get(0);
                    if (anonEntity.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                        this.email = CommonFactory.createEmailAddress(anon.get(0).getOwner());
                    } else {
                        throw new SecurityException("The object you requested was found, but its protection level was set to high to access. Try " +
                                "adding an email address and access key to your request.");
                    }
                }
            }
        }

    }

    protected EntityType getEntityType(HttpServletRequest req) {
        EntityType entityType = null;
        String type = req.getParameter(Parameters.type.getText());
        if (! StringUtils.isEmpty(type)) {
            Integer code = Integer.valueOf(type);
            entityType = EntityType.get(code);

        }
        if (entityType == null) {
            entityType = EntityType.point;
        }
        return entityType;
    }

    protected AccessKey createAccessKey(final Entity u, final AuthLevel authLevel) {

        final Entity en = EntityModelFactory.createEntity(u.getName(), "", EntityType.accessKey, ProtectionLevel.onlyMe,
                u.getName().getValue(), u.getName().getValue());
        return AccessKeyFactory.createAccessKey(en, u.getName().getValue(), u.getName().getValue(), authLevel);

    }







}
