package com.nimbits.client.model.summary;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:02 AM
 */
public class SummaryModelFactory {

    public static SummaryModel createSummary(Summary model) throws NimbitsException {
        return new SummaryModel(model);
    }

    public static SummaryModel createSummary(

            final String entity,
            final String targetPointUUID,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed) {
        return new SummaryModel(entity, targetPointUUID, summaryType, summaryIntervalMs, lastProcessed);

    }

    public static SummaryModel createSummary(
            final Entity e,
            final String entity,
            final String targetPointUUID,
            final SummaryType summaryType,
            final long summaryIntervalMs,
            final Date lastProcessed) throws NimbitsException {
        return new SummaryModel(e, entity, targetPointUUID, summaryType, summaryIntervalMs, lastProcessed);

    }
}
