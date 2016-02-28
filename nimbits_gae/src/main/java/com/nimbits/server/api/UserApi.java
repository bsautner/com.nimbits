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

import com.google.gson.Gson;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@Service
    public class UserApi extends HttpServlet {

        @Autowired
        private UserDao userDao;

        @Override
        public void init() throws ServletException {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


        }




        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            List<User> users = userDao.getAllUsers();
            Gson gson = GsonFactory.getInstance(true);
            String json = gson.toJson(users);

            PrintWriter out = resp.getWriter();

            out.print(json);

            out.close();


        }

    }

