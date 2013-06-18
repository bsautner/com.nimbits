package com.nimbits.cloudplatform.client.model.summary;

import com.nimbits.cloudplatform.client.enums.SummaryType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.trigger.TargetEntity;
import com.nimbits.cloudplatform.client.model.trigger.TriggerEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:02 AM
 */
public class SummaryModelFactory {

    private SummaryModelFactory() {
    }

    public static SummaryModel createSummary(Summary model)  {
        return new SummaryModel(model);
    }


    public static SummaryModel createSummary(
            final Entity e,
            final TriggerEntity entity,
            final TargetEntity target,
            final boolean enabled,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed)  {
        return new SummaryModel(e, entity, target, enabled, summaryType, summaryIntervalMs, lastProcessed);

    }

    public static List<Summary> createSummaries(List<Summary> result)  {
        List<Summary> retObj = new ArrayList<Summary>(result.size());
        for (Summary s : result) {
            retObj.add(createSummary(s));
        }
        return retObj;
    }
}
