package com.nimbits.client.service.summary;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;

public interface SummaryServiceAsync {
    void readSummary(Entity entity, AsyncCallback<Summary> async);

    void addUpdateSummary(Entity entity, Summary update, EntityName name, AsyncCallback<Entity> asyncCallback);

}
