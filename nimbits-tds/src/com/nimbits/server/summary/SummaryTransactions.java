package com.nimbits.server.summary;

import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 11:13 AM
 */
public interface SummaryTransactions {

    void addOrUpdateSummary(final Entity entity,final Summary summary);

    Summary readSummary(final Entity entity);

    void updateLastProcessed(final Entity entity);

    void deleteSummary(final Entity entity);
}
