/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.admin.quota;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.user.UserServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/12/12
 * Time: 10:00 AM
 */
@Transactional
@Component("quotaService")
public class QuotaServletImpl extends HttpServlet implements org.springframework.web.HttpRequestHandler  {



    private UserServiceImpl userService;

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }




    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String email = req.getParameter("email");
        final String amountParam =  req.getParameter("amount");

        try {

            double amount = Double.valueOf(amountParam);
            User user = userService.getUserByKey(email, AuthLevel.admin);
            userService.fundAccount(user, BigDecimal.valueOf(amount));
            resp.setStatus(Const.HTTP_STATUS_OK);


        } catch (NimbitsException e) {
            resp.setStatus(Const.HTTP_STATUS_INTERNAL_SERVER_ERROR);



        }
    }


}
