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

package com.nimbits.server.api.v3;


import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.user.User;
import com.nimbits.server.api.v3.actions.delete.DeleteAction;
import com.nimbits.server.api.v3.actions.get.GetAction;
import com.nimbits.server.api.v3.actions.post.PostAction;
import com.nimbits.server.api.v3.actions.put.PutAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class RestApi extends HttpServlet {

    @Autowired
    private GetAction getAction;

    @Autowired
    private PutAction putAction;

    @Autowired
    private PostAction postAction;

    @Autowired
    private DeleteAction deleteAction;



    @Override
    public void init() throws ServletException {

        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute(Parameters.user.getText());
        putAction.updateEntity(req, resp, user);

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        User user = (User) req.getAttribute(Parameters.user.getText());
        postAction.doPost(req, resp, user);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        User user = (User) req.getAttribute(Parameters.user.getText());
        deleteAction.doDelete(req, resp, user);


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getAttribute(Parameters.user.getText());
        getAction.doGet(req, resp, user);


    }







}
