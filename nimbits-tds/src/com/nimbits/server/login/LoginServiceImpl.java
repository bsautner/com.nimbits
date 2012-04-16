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

package com.nimbits.server.login;

import com.google.appengine.api.users.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.service.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;

import java.util.*;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public LoginInfo login(final String requestUri) throws NimbitsException {
        final UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        final LoginInfo loginInfo = new LoginInfo(); //TODO replace with factory

        if (user != null) {
            final EmailAddress internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(user.getEmail());

            loginInfo.setLoggedIn(true);
            loginInfo.setEmailAddress(internetAddress);
            loginInfo.setUserAdmin(userService.isUserAdmin());

            loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
            final List<Entity> list =   EntityTransactionFactory.getInstance(
                    com.nimbits.server.user.UserServiceFactory.getServerInstance().getAnonUser())
                    .getEntityByKey(internetAddress.getValue(), UserEntity.class);
            final com.nimbits.client.model.user.User  u;

            if (list.isEmpty()) {
                LogHelper.log(this.getClass(), "Created a new user");
                u = com.nimbits.server.user.UserServiceFactory.getServerInstance().createUserRecord(internetAddress);
               // sendUserCreatedFeed(u);
               // sendWelcomeFeed(u);
            }
            else {
               u = (com.nimbits.client.model.user.User) list.get(0);
            }
            u.setLastLoggedIn(new Date());
            EntityServiceFactory.getInstance().addUpdateEntity(u, u);

            loginInfo.setUser(u);

            // A user has logged in through google auth - this creates the user

        } else {
            loginInfo.setLoggedIn(false);
            loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
        }
        return loginInfo;
    }



    private static void sendWelcomeFeed(com.nimbits.client.model.user.User u) throws NimbitsException {

        final String message =
                "<b>Welcome To Nimbits!</b> <br> This is your data feed channel, you can subscribe " +
                "to data points and see events like high and low alerts here. You can get started by creating " +
                "a new data point using the File menu. Right click on the point to configure its compression, " +
                "alerts, calculations etc. " +
                "You can find other shared data points on <a href=\"http://www.nimbits.com\" " +
                "target=\"_blank\" >nimbits.com</a> and subscribe to their " +
                "alerts. Use the connection request button to invite other Nimbits users to connect to your account so " +
                "you can see each others data.";
        FeedServiceFactory.getInstance().postToFeed(u, message, FeedType.info);
    }
    private static void sendUserCreatedFeed(com.nimbits.client.model.user.User u) throws NimbitsException {

        final String message =
                "New Nimbits user registered successfully";
        FeedServiceFactory.getInstance().postToFeed(u, message, FeedType.info);
    }

}