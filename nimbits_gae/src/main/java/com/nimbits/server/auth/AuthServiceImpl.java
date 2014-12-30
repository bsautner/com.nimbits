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
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    public List<EmailAddress> getCurrentUser(HttpServletRequest request) {
        com.google.appengine.api.users.UserService googleUserService;
        googleUserService = UserServiceFactory.getUserService();

        if (googleUserService != null) {
            googleUserService = UserServiceFactory.getUserService();
            com.google.appengine.api.users.User appUser = googleUserService.getCurrentUser();
            if (appUser != null) {
                String email = googleUserService.getCurrentUser().getEmail();
                if (!StringUtils.isEmpty(email)) {
                    return Arrays.asList(CommonFactory.createEmailAddress(email));
                }

            }

        }
        return Collections.emptyList();
    }
}
