/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.process.cron;

import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.admin.quota.Quota;
import com.nimbits.server.admin.quota.QuotaFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:04 PM
 */
public class QuotaResetCron  extends HttpServlet {

    private static final long serialVersionUID = 2L;
    private static final Logger log = Logger.getLogger(QuotaResetCron.class.getName());


    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            Quota quota = QuotaFactory.getInstance();
            quota.resetCounters();
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }


    }




}
