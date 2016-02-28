/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api;

import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class MobileApi extends HttpServlet {

    private final static String SHARED_KEY = "EY7xXH%&%v3fZbz7ye!rPYqyt:bq.E<arWc;SS^bS^TC#qQ!SLuV=XESX$3YKcUE4aQK@d)__tnc(;rCX+PjRB{!/x+SRseVkrjmgY2-Nr8%~<?bq_(3p3u~#}T)8k,vxv+[jR2vw%;$.p)x]&(Yxa9fBY%-3.uy4qDZ9bPcJf5q+JM@u9,)2NqcPq-Arseh*p^6wj;2mUKd<D=>CDz#KrU289evW(AgfW]M(K#f.<J8-Mey}5";

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityService entityService;


    @Autowired
    private ValueService valueService;


    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }




    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String email = req.getParameter("encoded_e");
        String code = req.getParameter("enc");

        if (! SHARED_KEY.equals(code) || StringUtils.isEmpty(email)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
            User user = getUser(email);

        }


    }

    private User getUser(String email) {

        try {
           return userDao.getUserByEmail(email);
        } catch (Throwable throwable) {
            return userService.createUserRecord(entityService, valueService, CommonFactory.createEmailAddress(email), SHARED_KEY, UserSource.google);

        }
    }

}