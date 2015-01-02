/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

package com.nimbits.server.auth;

import com.google.appengine.api.users.UserServiceFactory;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    public List<EmailAddress> getCurrentUser(HttpServletRequest request) {
        com.google.appengine.api.users.UserService googleUserService;
        googleUserService = UserServiceFactory.getUserService();
        List<EmailAddress> result = new ArrayList<EmailAddress>(1);

        EmailAddress emailAddress;

        if (request != null && request.getSession() != null) {
            String email = (String) request.getSession().getAttribute(Const.LOGGED_IN_EMAIL);
            if (email != null) {
                emailAddress =  CommonFactory.createEmailAddress(email);
                result.add(emailAddress);
            }
        }



        if (result.isEmpty() && googleUserService != null) {
            googleUserService = UserServiceFactory.getUserService();
            com.google.appengine.api.users.User appUser = googleUserService.getCurrentUser();
            if (appUser != null) {
                String email = googleUserService.getCurrentUser().getEmail();
                if (!StringUtils.isEmpty(email)) {
                    result.add(CommonFactory.createEmailAddress(email));
                }

            }

        }
        return result;
    }

    @Override
    public boolean isGAE() {
        return true;
    }

    @Override
    public boolean isGAEAdmin() {
        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();
        final com.google.appengine.api.users.User googleUser = gaeUserService.getCurrentUser();
        if (googleUser != null) {
            return gaeUserService.isUserAdmin();

        }
        else {
            return false;
        }
    }

    @Override
    public String createLoginURL(String requestUri) {
        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();

        return gaeUserService.createLoginURL(requestUri);
    }

    @Override
    public String createLogoutURL(String requestUri) {
        final com.google.appengine.api.users.UserService gaeUserService = com.google.appengine.api.users.UserServiceFactory.getUserService();

        return gaeUserService.createLogoutURL(requestUri);
    }

    @Override
    public Transport getMailTransport() {
        return null;
    }


}
