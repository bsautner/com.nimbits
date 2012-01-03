package com.nimbits.server.cron;

import com.google.appengine.api.datastore.*;
import com.nimbits.client.model.Const;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

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
    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;

    private static final Logger log = Logger.getLogger(SessionMaint.class.getName());
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        final Set<Key> keys = new HashSet<Key>();
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
