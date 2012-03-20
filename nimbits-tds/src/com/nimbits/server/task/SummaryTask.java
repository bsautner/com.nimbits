package com.nimbits.server.task;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.point.*;
import com.nimbits.server.value.*;
import com.nimbits.server.summary.*;
import com.nimbits.server.user.*;
import org.apache.commons.math3.stat.descriptive.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:35 PM
 */
public class SummaryTask  extends HttpServlet {

    private static final long serialVersionUID = 1L;



    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String json = req.getParameter(Const.Params.PARAM_JSON);
        final Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

        final User user = UserServiceFactory.getInstance().getUserByUUID(entity.getOwner());
        final Summary summary = SummaryTransactionFactory.getInstance(user).readSummary(entity);
        final Date now = new Date();
        final long d = new Date().getTime() - summary.getSummaryIntervalMs();
        if (summary.getLastProcessed().getTime() < d) {
            final Point source = PointServiceFactory.getInstance().getPointByUUID(summary.getEntity());
            final Timespan span = TimespanModelFactory.createTimespan(new Date(now.getTime() - summary.getSummaryIntervalMs()), now);
            final List<Value> values = RecordedValueServiceFactory.getInstance().getDataSegment(source, span);
            final double[] doubles = new double[values.size()];
            for (int i = 0; i< values.size(); i++) {
                doubles[i] = values.get(i).getNumberValue();
            }
            if (values.size() > 0) {
                // final Entity targetEntity = EntityServiceFactory.getInstance().getEntityByUUID(summary.getTargetPointUUID());
                final Point target = PointServiceFactory.getInstance().getPointByUUID(summary.getTargetPointUUID());
                final double result = getValue(summary.getSummaryType(), doubles);
                final Value value = ValueModelFactory.createValueModel(result);
                RecordedValueServiceFactory.getInstance().recordValue(user, target, value, false);
                SummaryServiceFactory.getInstance().updateLastProcessed(entity);
            }

        }
    }

    protected double getValue(SummaryType type, double[] doubles) {
        DescriptiveStatistics d = new DescriptiveStatistics(doubles);

        switch (type) {

            case average:
                return d.getMean();
            case standardDeviation:
                return d.getStandardDeviation();
            case max:
                return d.getMax();
            case min:
                return d.getMin();
            case skewness:
                return d.getSkewness();
            case sum:
                return d.getSum();
            case variance:
                return d.getVariance();

            default:
                return 0;
        }
    }




}
