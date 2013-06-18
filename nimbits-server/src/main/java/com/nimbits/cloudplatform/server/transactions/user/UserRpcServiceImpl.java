package com.nimbits.cloudplatform.server.transactions.user;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.email.EmailAddress;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.user.UserModelFactory;
import com.nimbits.cloudplatform.client.service.user.UserService;
import com.nimbits.cloudplatform.server.admin.logging.LogHelper;
import com.nimbits.cloudplatform.server.api.openid.UserInfo;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/2/13
 * Time: 6:27 PM
 */
@Service("userService")
public class UserRpcServiceImpl extends RemoteServiceServlet implements UserService {
    @Override
    public User loginRpc(final String requestUri) throws Exception {

        final User retObj;
        EmailAddress internetAddress = null;
        boolean isAdmin = false;
        UserInfo domainUser = null;
        if (this.getThreadLocalRequest() != null) {
            domainUser = (UserInfo) this.getThreadLocalRequest().getSession().getAttribute("user");
        }
        final com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();

        if (domainUser != null) {
            internetAddress = CommonFactory.createEmailAddress(domainUser.getEmail());
        } else {

            final com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
            if (googleUser != null) {
                isAdmin = userService.isUserAdmin();
                internetAddress = CommonFactory.createEmailAddress(googleUser.getEmail());
            }
        }

        if (internetAddress != null) {

            final List<Entity> list = EntityServiceImpl
                    .getEntityByKey(
                            UserTransaction.getAnonUser(), internetAddress.getValue(), EntityType.user);


            if (list.isEmpty()) {
                // log.severe("new user on" + this.getThreadLocalRequest().getRequestURI());
                LogHelper.log(this.getClass(), "Created a new user");
                retObj = UserTransaction.createUserRecord(internetAddress);
                // sendUserCreatedFeed(u);
                // sendWelcomeFeed(u);
            } else {
                retObj = (User) list.get(0);
            }

            retObj.setLoggedIn(true);

            retObj.setUserAdmin(isAdmin);

            retObj.setLogoutUrl(userService.createLogoutURL(requestUri));

            retObj.setLastLoggedIn(new Date());
            EntityServiceImpl.addUpdateEntity(retObj, Arrays.<Entity>asList(retObj));
            retObj.addAccessKey(UserTransaction.authenticatedKey(retObj));


        } else {
            final EntityName name = CommonFactory.createName("anon@nimbits.com", EntityType.user);
            final Entity e = EntityModelFactory.createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe, "", "");
            retObj = UserModelFactory.createUserModel(e);
            retObj.setLoggedIn(false);
            retObj.setLoginUrl(userService.createLoginURL(requestUri));
        }
        return retObj;
    }






}
