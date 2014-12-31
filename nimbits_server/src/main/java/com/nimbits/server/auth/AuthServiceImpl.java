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

import com.nimbits.client.constants.Const;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    public List<EmailAddress> getCurrentUser(HttpServletRequest request) {

        List<EmailAddress> result = new ArrayList<EmailAddress>(1);

        EmailAddress emailAddress;

        if (request != null && request.getSession() != null) {
            String email = (String) request.getSession().getAttribute(Const.LOGGED_IN_EMAIL);
            if (email != null) {
                emailAddress =  CommonFactory.createEmailAddress(email);
                result.add(emailAddress);
            }
        }

        //todo - use filter to check here for auth token and query dao for user if provided


        return result;
    }

    @Override
    public boolean isGAE() {
        return false;
    }

    @Override
    public boolean isGAEAdmin() {
       return false;
    }

    @Override
    public String createLoginURL(String requestUri) {
           return Const.WEBSITE;
    }

    @Override
    public String createLogoutURL(String requestUri) {
        return Const.WEBSITE;
    }
}
