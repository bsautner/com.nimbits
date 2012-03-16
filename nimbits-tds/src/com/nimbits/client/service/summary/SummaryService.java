package com.nimbits.client.service.summary;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:05 AM
 */
@RemoteServiceRelativePath("summary")
public interface SummaryService  extends RemoteService {

    Summary readSummary(Entity entity) throws NimbitsException;

    Entity addUpdateSummary(Entity entity, Summary update, EntityName name) throws NimbitsException;
}
