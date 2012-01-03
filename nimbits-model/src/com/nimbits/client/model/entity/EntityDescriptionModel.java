package com.nimbits.client.model.entity;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.server.Server;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 4:52 PM
 */
public class EntityDescriptionModel implements Serializable, EntityDescription {

    private int idPoint;

    private Server server;

    private String pointName;

    private String uuid;

    private String pointDesc;

    private int entityType;

    public EntityDescriptionModel(Server server, PointName name, String uuid, String pointDesc, EntityType entityType) {
        this.server = server;
        this.pointName = name.getValue();
        this.uuid = uuid;
        this.pointDesc = pointDesc;
        this.entityType = entityType.getCode();
    }

    public EntityDescriptionModel(Server server, CategoryName name, String uuid, String pointDesc, EntityType entityType) {
        this.server = server;
        this.pointName = name.getValue();
        this.uuid = uuid;
        this.pointDesc = pointDesc;
        this.entityType = entityType.getCode();
    }

    public EntityDescriptionModel(EntityDescription p) {
        this.server = p.getServer();
        this.pointName = p.getName();
        this.uuid = p.getUuid();
        this.pointDesc = p.getDesc();
        this.idPoint = p.getIdPoint();
        this.entityType = p.getEntityType().getCode();
    }

    @Override
    public int getIdPoint() {
        return this.idPoint;
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.pointName;
    }

    @Override
    public String getDesc() {
        return this.pointDesc;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }
}
