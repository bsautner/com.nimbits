package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.user.User;

import java.util.ArrayList;
import java.util.List;

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
                                      final String ownerUUID) {
        return new EntityModel(name, description, entityType, protectionLevel, entityUUID, parentUUID,
                ownerUUID, "");
    }
    public static Entity createEntity(final EntityName name,
                                      final String description,
                                      final EntityType entityType,
                                      final ProtectionLevel protectionLevel,
                                      final String entityUUID,
                                      final String parentUUID,
                                      final String ownerUUID,
                                      final String blobKey) {
        return new EntityModel(name, description, entityType, protectionLevel, entityUUID, parentUUID,
                ownerUUID, blobKey);
    }


    public static Entity createEntity(User user) {
        EntityName name = CommonFactoryLocator.getInstance().createName(user.getEmail().getValue());

        return createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                user.getUuid(), user.getUuid(), user.getUuid());

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

    public static List<Entity> createEntities(User user, List<Entity> result) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (Entity e : result) {
            boolean isOwner = e.getOwner().equals(user.getUuid());


            if (entityIsReadable(user, e, isOwner))
            {

                Entity r = createEntity(e);
                r.setReadOnly(!isOwner);
                entities.add(r);
            }

        }
        return entities;

    }

    private static boolean entityIsReadable(User user, Entity e, boolean owner) {
        boolean retVal =  ((e.getEntityType().equals(EntityType.user) ||
                owner ||
                e.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                e.getProtectionLevel().equals(ProtectionLevel.onlyConnection))

        );

        if (e.getEntityType().equals(EntityType.userConnection) && ! e.getOwner().equals(user.getUuid())) {
            retVal = false;
        }

        return retVal;


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



}
