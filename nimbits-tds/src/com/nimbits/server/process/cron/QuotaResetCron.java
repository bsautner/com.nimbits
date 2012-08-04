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

import com.google.appengine.api.datastore.*;
import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.admin.quota.Quota;
import com.nimbits.server.admin.quota.QuotaFactory;
import com.nimbits.server.admin.system.SystemServiceFactory;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:04 PM
 */
public class QuotaResetCron  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(QuotaResetCron.class.getName());
    private static final int LIMIT = 5000;
    private static final int INT = 1024;

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            processGet(resp);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }


    }

    protected static void processGet(ServletResponse resp) throws NimbitsException, IOException {


        final DatastoreService store = DatastoreServiceFactory.getDatastoreService();

        final Query q = new Query("UserEntity").setKeysOnly();

        int count = 0;
        for (final Entity e : store.prepare(q).asList(FetchOptions.Builder.withLimit(LIMIT))) {
            EmailAddress em = CommonFactoryLocator.getInstance().createEmailAddress(e.getKey().getName());
            count++;

            Quota quota = QuotaFactory.getInstance(em);
            int c = quota.getCount();

            quota.resetCounter();

        }

        SystemServiceFactory.getInstance().updateSystemPoint("UserCount", count, false);

    }



}
