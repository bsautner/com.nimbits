package com.nimbits.server.summary;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.summary.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.user.*;

import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:08 AM
 */
public class SummaryServiceImpl  extends RemoteServiceServlet implements SummaryService {

    private User getUser() throws NimbitsException {

        return UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

    }

    @Override
    public Summary readSummary(Entity entity) throws NimbitsException {
        return   SummaryTransactionFactory.getInstance(getUser()).readSummary(entity);
    }

    @Override
    public void updateLastProcessed(Entity entity) {
        SummaryTransactionFactory.getInstance(null).updateLastProcessed(entity);

    }

    @Override
    public void deleteSummary(User u, Entity entity) {
       SummaryTransactionFactory.getInstance(u).deleteSummary(entity);
    }

    @Override
    public Entity addUpdateSummary(final Entity entity,final Summary update,final EntityName name) throws NimbitsException {
        User u = getUser();

        if (entity.getEntityType().equals(EntityType.point)) {
            String uuid = UUID.randomUUID().toString();
            Entity newEntity = EntityModelFactory.createEntity(name, "", EntityType.summary,
                    ProtectionLevel.onlyMe, uuid, entity.getEntity(), u.getUuid());
            Entity createdEntity = EntityServiceFactory.getInstance().addUpdateEntity(u, newEntity);
            Summary newSummary = SummaryModelFactory.createSummary(uuid, entity.getEntity(), update.getTargetPointUUID(), update.getSummaryType(),
                    update.getSummaryIntervalMs(), new Date());

            SummaryTransactionFactory.getInstance(u).addOrUpdateSummary(createdEntity, newSummary);
            return createdEntity;
        }
        else {
            EntityServiceFactory.getInstance().addUpdateEntity(u, entity);
            SummaryTransactionFactory.getInstance(u).addOrUpdateSummary(entity, update);
            return entity;
        }

    }

}
