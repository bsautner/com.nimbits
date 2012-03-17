package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.*;
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


    public static Entity createEntity(final User user, final Entity entity) {
        Entity r = new EntityModel(entity);
        boolean isOwner =  (user != null && entity.getOwner().equals(user.getUuid()));
        r.setReadOnly(!isOwner);
        return r;

    }

    public static Entity createEntity(final Entity entity) {

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


    public static Entity createEntity(final User user) throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName(user.getEmail().getValue(), EntityType.user);

        return createEntity(name, "", EntityType.user, ProtectionLevel.onlyMe,
                user.getUuid(), user.getUuid(), user.getUuid());

    }


    public static EntityDescription createEntityDescription(final Server server, Entity entity) {
        return new EntityDescriptionModel(server, entity);
    }

    public static List<EntityDescription> createPointDescriptions(final List<EntityDescription> entityDescriptions) {
        List<EntityDescription> retObj = new ArrayList<EntityDescription>();
        for (EntityDescription entityDescription : entityDescriptions) {
            retObj.add(createPointDescription(entityDescription));
        }
        return retObj;

    }

    public static EntityDescription createPointDescription(final EntityDescription entityDesc) {

        return new EntityDescriptionModel(entityDesc);


    }

    public static List<Entity> createEntities(final User user,final  List<Entity> result) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (final Entity e : result) {

            final boolean isOwner = (user != null) && e.getOwner().equals(user.getUuid());

            if (entityIsReadable(user, e, isOwner))
            {

                Entity r = createEntity(user, e);

                entities.add(r);
            }

        }
        return entities;

    }

    private static boolean entityIsReadable(final User user, final Entity e, final boolean owner) {
        boolean retVal =  ((e.getEntityType().equals(EntityType.user) ||
                owner ||
                e.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                e.getProtectionLevel().equals(ProtectionLevel.onlyConnection))

        );

        if (e.getEntityType().equals(EntityType.userConnection) && ! e.getOwner().equals(user.getUuid())) {
            retVal = false;
        }
        if (e.getEntityType().equals(EntityType.summary) && user == null) {
            retVal = true; //this is a system request from the summary cron job.
        }
        return retVal;


    }

    public static Entity createEntity(final EntityName name, final EntityType entityType) {
        return new EntityModel(name,
                "",
                entityType,
                ProtectionLevel.everyone,
                null,
                null,
                null,
                null);
    }


    public static Entity createEntity(final User u, final Point p, final EntityName name) {
        if (u != null) {
            return new EntityModel(name,
                    "",
                    EntityType.point,
                    ProtectionLevel.everyone,
                    p.getUUID(),
                    u.getUuid(),
                    u.getUuid(),
                    u.getUuid());
        }
        else {
            return new EntityModel(name,
                    "",
                    EntityType.point,
                    ProtectionLevel.everyone,
                    p.getUUID(),
                    null,
                    null,
                    null);
        }
    }



}
