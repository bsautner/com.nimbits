package com.nimbits.client.service.summary;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:05 AM
 */
@RemoteServiceRelativePath("summary")
public interface SummaryService  extends RemoteService {

    Summary readSummary(final Entity entity) throws NimbitsException;

    Entity addUpdateSummary(final Entity entity, final Summary update, final EntityName name) throws NimbitsException;

    void updateLastProcessed(final Entity entity);

    void deleteSummary(final User u, final Entity entity);
}
