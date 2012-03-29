package com.nimbits.server.cron;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.task.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:16 PM
 */
public class SummaryCron  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SummaryCron.class.getName());

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Map<String, Entity> result = null;
        try {
            result = EntityServiceFactory.getInstance().getSystemWideEntityMap(EntityType.summary);

        for (Entity entity : result.values()) {
            TaskFactoryLocator.getInstance().startSummaryTask(entity);


        }
        } catch (NimbitsException e) {
          log.severe(e.getMessage());
        }

    }

}
