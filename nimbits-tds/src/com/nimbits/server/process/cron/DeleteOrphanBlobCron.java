package com.nimbits.server.process.cron;

import com.nimbits.server.process.task.*;

import javax.servlet.http.*;
import java.io.*;
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

        TaskFactory.getInstance().startDeleteOrphanedBlobTask(null);

    }
}
