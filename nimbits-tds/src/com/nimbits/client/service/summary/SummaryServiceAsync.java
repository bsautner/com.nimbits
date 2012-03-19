package com.nimbits.client.service.summary;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;

public interface SummaryServiceAsync {
    void readSummary(Entity entity, AsyncCallback<Summary> async);

    void addUpdateSummary(Entity entity, Summary update, EntityName name, AsyncCallback<Entity> asyncCallback);

    void updateLastProcessed(Entity entity, AsyncCallback<Void> async);

    void deleteSummary(final User u, final Entity entity, AsyncCallback<Void> async);
}
