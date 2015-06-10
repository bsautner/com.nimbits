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

package com.nimbits.server.process.task;

import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.point.Point;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PointTask extends HttpServlet {

    private final Logger logger = Logger.getLogger(PointTask.class.getName());

    @Autowired
    protected EntityService entityService;


    @Autowired
    protected EntityDao entityDao;

    @Autowired
    protected ValueService valueService;


    @Autowired
    public TaskService taskService;

    @Autowired
    public UserService userService;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {

        processRequest(req);


    }

    //TODO only process active points, use the point count instead

    private void processRequest(HttpServletRequest req) {
        String cursor = req.getParameter(Parameters.cursor.getText());
        long position = Long.valueOf(cursor);

        List<Point> points = entityDao.getPoint(position);
        if (! points.isEmpty()) {
            logger.info("Point Maint: " + position);


        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req);
    }
}

