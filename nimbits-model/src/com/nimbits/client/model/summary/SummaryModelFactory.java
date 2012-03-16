package com.nimbits.client.model.summary;

import com.nimbits.client.enums.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:02 AM
 */
public class SummaryModelFactory {

    public static SummaryModel createSummary(Summary model) {
        return new SummaryModel(model);
    }

    public static SummaryModel createSummary(
            final String uuid,
            final String entity,
            final String targetPointUUID,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed) {
        return new SummaryModel(uuid, entity, targetPointUUID, summaryType, summaryIntervalMs, lastProcessed);

    }
}
