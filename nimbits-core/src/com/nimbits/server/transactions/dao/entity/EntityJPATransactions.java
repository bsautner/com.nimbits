package com.nimbits.server.transactions.dao.entity;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.orm.JpaEntity;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 1:04 PM
 */
public interface EntityJPATransactions {
    Entity addEntity(final Entity entity, final String instanceUrl) throws NimbitsException;

    Entity addUpdateEntity(final Entity entity,final String instanceUrl) throws NimbitsException;

    List<Entity> searchEntity(final String searchText);

    Entity getEntityByUUID(final String uuid) throws NimbitsException;

    void deleteEntityByUUID(final String uuid);

    void updateLocation(final Entity entity,final String location);

    String getLocation(Entity entity);

    List<JpaEntity> getAllEntities();

    List<String[]> getLocations();
}
