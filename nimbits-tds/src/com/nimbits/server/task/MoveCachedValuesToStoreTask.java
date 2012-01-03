package com.nimbits.server.task;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.recordedvalue.RecordedValueTransactionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/20/11
 * Time: 3:55 PM
 */
public class MoveCachedValuesToStoreTask extends HttpServlet

{

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MoveCachedValuesToStoreTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String pointJson = req.getParameter(Const.PARAM_POINT);
        final Point point = GsonFactory.getInstance().fromJson(pointJson, PointModel.class);
        try {
            RecordedValueTransactionFactory.getInstance(point).moveValuesFromCacheToStore();
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }
        log.info("Moved Mem Cache to Store: " + point.getName().getValue());
    }
}
