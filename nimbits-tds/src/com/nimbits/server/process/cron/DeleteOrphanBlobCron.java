package com.nimbits.server.process.cron;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.memcache.*;
import com.nimbits.server.process.task.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 2:00 PM
 */
public class DeleteOrphanBlobCron  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointCron.class.getName());
    private static MemcacheService cache;
    private static final String LAST_CHECKED_KEY = "LAST_CHECKED_KEY";

    static {
        cache =  MemcacheServiceFactory.getMemcacheService();
    }


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        processRequest();
    }

    protected  static int processRequest() throws IOException {
        Iterator<BlobInfo> iterator;
        if (cache.contains(LAST_CHECKED_KEY)) {
            iterator = new BlobInfoFactory().queryBlobInfosAfter((BlobKey) cache.get(LAST_CHECKED_KEY));
            log.info("starting with " + cache.get(LAST_CHECKED_KEY));
        } else {
            iterator = new BlobInfoFactory().queryBlobInfos();
            log.info("starting with first blob ");
        }


        int count = 0;
        while (iterator.hasNext()){
            final BlobInfo i = iterator.next();
            log.info("checking : " + i.getBlobKey());
            TaskFactory.getInstance().startDeleteOrphanedBlobTask(i.getBlobKey());
           // cache.delete(LAST_CHECKED_KEY);
            //cache.put(LAST_CHECKED_KEY, i.getBlobKey());
           // count++;


        }
        return count;
    }
}
