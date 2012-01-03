package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.server.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 9:51 AM
 */
public class EntityModelFactory {

//
//    public static EntityDescription createEntityDescription(Server server, PointName name, String uuid, String pointDesc) {
//
//        return new EntityDescriptionModel(server, name, uuid, pointDesc, EntityType.point);
//
//
//    }
//    public static EntityDescription createEntityDescription(Server server, CategoryName  name, String uuid, String pointDesc) {
//
//        return new EntityDescriptionModel(server, name, uuid, pointDesc, EntityType.category);
//
//
//    }

    public static EntityDescription createEntityDescription(Server server, PointName name, String uuid, String pointDesc, EntityType type) {

        return new EntityDescriptionModel(server, name, uuid, pointDesc, type);


    }

    public static EntityDescription createEntityDescription(Server server, CategoryName name, String uuid, String pointDesc, EntityType type) {

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

}
