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
import com.nimbits.client.constants.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/19/11
 * Time: 7:38 PM
 */
public class SessionMaint extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID =1L;

    private static final Logger log = Logger.getLogger(SessionMaint.class.getName());
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        final Collection<Key> keys = new HashSet<Key>(1000);
        final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
        final Query q = new Query("_ah_SESSION").setKeysOnly();
        int count=0;
        for (final Entity e : store.prepare(q).asList(FetchOptions.Builder.withLimit(1000))) {
            count++;
            keys.add(e.getKey());
        }


        store.delete(keys);
        log.info("Deleted " + count + " sessions");




    }

}
