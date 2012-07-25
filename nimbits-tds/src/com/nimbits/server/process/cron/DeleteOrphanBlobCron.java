package com.nimbits.server.process.cron;

import com.google.appengine.api.blobstore.*;
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


    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
            processRequest();
    }

    protected  static void processRequest() throws IOException {
//        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
//
//        if  (iterator.hasNext()){
//            final BlobInfo i = iterator.next();
//            TaskFactory.getInstance().startDeleteOrphanedBlobTask(i.getBlobKey());
//
//        }


    }
}
