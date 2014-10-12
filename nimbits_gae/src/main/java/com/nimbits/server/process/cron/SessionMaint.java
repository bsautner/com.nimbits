/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.process.cron;

import com.google.appengine.api.datastore.*;
import com.nimbits.client.constants.Const;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;


public class SessionMaint extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(SessionMaint.class.getName());

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        final Collection<Key> keys = new HashSet<Key>(1000);
        final DatastoreService store = DatastoreServiceFactory.getDatastoreService();
        final Query q = new Query("_ah_SESSION").setKeysOnly();
        int count = 0;
        for (final Entity e : store.prepare(q).asList(FetchOptions.Builder.withLimit(1000))) {
            count++;
            keys.add(e.getKey());
        }


        store.delete(keys);
        log.info("Deleted " + count + " sessions");


    }


}
