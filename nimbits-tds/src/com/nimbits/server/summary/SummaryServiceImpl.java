/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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

            Entity newEntity = EntityModelFactory.createEntity(name, "", EntityType.summary,
                    ProtectionLevel.onlyMe, entity.getKey(), u.getKey());
            Entity createdEntity = EntityServiceFactory.getInstance().addUpdateEntity(u, newEntity);
            Summary newSummary = SummaryModelFactory.createSummary(newEntity.getKey(), entity.getKey(), update.getTargetPointUUID(), update.getSummaryType(),
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
