package com.nimbits.server.entity;

import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.server.user.*;

import javax.servlet.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityService {
    private User u;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            u = null;
        }
    }

    @Override
    public List<Entity> getEntities() {
         return EntityTransactionFactory.getInstance(u).getEntities();
    }

    @Override
    public Entity addUpdateEntity(Entity entity) {
        if (entity.getOwnerUUID() == null) {
            entity.setOwnerUUID(u.getUuid());
        }
        if (entity.getParentUUID() == null) {
            entity.setParentUUID(u.getUuid());
        }
        if (entity.getUUID() == null) {
            entity.setUUID(UUID.randomUUID().toString());
        }
        return EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
    }

    @Override
    public void deleteEntity(Entity entity) {
        EntityTransactionFactory.getInstance(u).deleteEntity(entity);
    }

    @Override
    public Entity getEntityByUUID(String uuid) {
       return EntityTransactionFactory.getInstance(u).getEntityByUUID(uuid);
    }

    @Override
    public Map<String, Entity> getEntityMap(EntityType type, boolean includeValues) {
       return EntityTransactionFactory.getInstance(u).getEntityMap(type, includeValues);
    }


}
