package com.nimbits.client.model.entity;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.server.*;
import com.nimbits.client.model.user.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 9:51 AM
 */
public class EntityModelFactory {

    public static Entity createEntity(Entity entity) {
        return new EntityModel(entity);
    }
    public static Entity createEntity(final EntityName name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String entityUUID,
                       final String parentUUID,
                       final String childUUID,
                       final String ownerUUID) {
        return new EntityModel(name, description, entityType, protectionLevel, entityUUID, parentUUID,
                childUUID, ownerUUID);
    }

    public static Entity createEntity(User user) {
        EntityName name = CommonFactoryLocator.getInstance().createName(user.getEmail().getValue());

        return createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                user.getUuid(), "", user.getUuid(), user.getUuid());

    }


    public static EntityDescription createEntityDescription(Server server, EntityName name, String uuid, String pointDesc, EntityType type) {
         return new EntityDescriptionModel(server, name, uuid, pointDesc, type);
    }

    public static List<EntityDescription> createPointDescriptions(List<EntityDescription> entityDescriptions) {
        List<EntityDescription> retObj = new ArrayList<EntityDescription>();
        for (EntityDescription entityDescription : entityDescriptions) {
            retObj.add(createPointDescription(entityDescription));
        }
        return retObj;

     }

    public static EntityDescription createPointDescription(EntityDescription entityDesc) {

        return new EntityDescriptionModel(entityDesc);


    }

    public static List<Entity> createEntities(List<Entity> result) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (Entity e : result) {
            entities.add(createEntity(e));
        }
        return entities;

    }

    public static Entity createEntity(EntityName name, EntityType entityType) {
        return new EntityModel(name,
                "",
                entityType,
                ProtectionLevel.everyone,
                null,
                null,
                null,
                null);
    }
   @Deprecated
    public static Entity createEntity(Point p) {
        return new EntityModel(p.getName(),
                "",
                EntityType.point,
                ProtectionLevel.everyone,
                p.getUUID(),
                null,
                null,
                null);
    }
    @Deprecated
    public static Entity createEntity(Diagram d) {
        return new EntityModel(d.getName(),
                "",
                EntityType.diagram,
                ProtectionLevel.everyone,
                d.getUuid(),
                null,
                null,
                null);
    }
}
