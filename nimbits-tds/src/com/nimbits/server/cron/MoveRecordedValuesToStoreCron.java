package com.nimbits.server.cron;

import com.nimbits.client.model.point.*;
import com.nimbits.server.point.*;
import com.nimbits.server.task.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 5:24 PM
 */
public class MoveRecordedValuesToStoreCron extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // MemcacheService systemCache;

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        PrintWriter out = resp.getWriter();
        int count = 0;
        final List<Point> points = PointServiceFactory.getInstance().getAllPoints();
        count++;
        StringBuilder sb = new StringBuilder();
        for (final Point point : points) {
           sb.append("<p>" + point.getName() + "</p>");
            TaskFactoryLocator.getInstance().startMoveCachedValuesToStoreTask(point);
        }

        out.print("<h4> Total Points (using datastore): " + count + "</h4>");
        out.print(sb.toString());

    }
}
