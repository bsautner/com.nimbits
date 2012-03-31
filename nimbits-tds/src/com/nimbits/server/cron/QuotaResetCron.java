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

package com.nimbits.server.cron;

import com.google.appengine.api.datastore.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.quota.*;
import com.nimbits.server.settings.*;
import com.nimbits.server.task.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:04 PM
 */
public class QuotaResetCron  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(QuotaResetCron.class.getName());

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            processGet();
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }


    }

    protected void processGet() throws NimbitsException {
        final List<User> users = UserTransactionFactory.getInstance().getUsers();
        for (final User u : users) {
            QuotaFactory.getInstance(u).resetCounter();
        }
    }


}
