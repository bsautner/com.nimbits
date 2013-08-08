package com.nimbits.cloudplatform.client.model.entity;


import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.enums.*;
import com.nimbits.cloudplatform.client.model.accesskey.AccessKey;
import com.nimbits.cloudplatform.client.model.common.CommonIdentifier;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.user.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 11:06 AM
 */
@SuppressWarnings("InstanceofInterfaces")
public class EntityModel  implements Serializable, Comparable<Entity>, Entity {


    private String name;
    private String key;
    private String description;
    private int entityType;
    private int protectionLevel;
    private int alertType;
    private String parent;
    private String owner;
    private boolean readOnly = false;
    private String uuid;
    private Date dateCreated;
    private static final long serialVersionUID =3455345354L;
    private List<Point> children;
    private String instanceUrl;
    private boolean isCached = false;


    public EntityModel(final CommonIdentifier name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String parent,
                       final String owner,
                       final String uuid) {
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;
        this.protectionLevel = protectionLevel.getCode();
        this.alertType = AlertType.OK.getCode();
        this.uuid = uuid;
        this.dateCreated = new Date();

    }

    public EntityModel() {
    }
    public EntityModel(final Entity anEntity)  {
        if (anEntity != null) {
            this.dateCreated = new Date(anEntity.getDateCreated().getTime());
            this.key = anEntity.getKey();
            this.name = anEntity.getName().getValue();
            this.description = anEntity.getDescription();
            this.entityType = anEntity.getEntityType().getCode();
            this.parent = anEntity.getParent();
            this.owner = anEntity.getOwner();
            this.protectionLevel = anEntity.getProtectionLevel().getCode();
            this.alertType = anEntity.getAlertType().getCode();
            this.uuid = anEntity.getUUID();
            this.instanceUrl = anEntity.getInstanceUrl();


        }
    }


    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public String getInstanceUrl() {
        return instanceUrl;
    }

    @Override
    public boolean isCached() {
        return this.isCached;

    }

    @Override
    public void setIsCached(boolean isCached) {
        this.isCached = isCached;
    }

    @Override
    public List<Point> getChildren() {
        return children;
    }

    @Override
    public void setChildren(final List<Point> someChildren) {
        this.children = someChildren;
    }

    @Override
    public void update(Entity update) {
        this.description = update.getDescription();
        this.name = update.getName().getValue();
        this.protectionLevel = update.getProtectionLevel().getCode();
        this.parent = update.getParent();
        this.uuid = update.getUUID();
    }


    @Override
    public EntityName getName()   {

            return CommonFactory.createName(name, EntityType.get(entityType));

    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setName(final EntityName name) {
        this.name = name.getValue();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }

    @Override
    public void setEntityType(final EntityType entityType) {
        this.entityType = entityType.getCode();
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void setParent(final String parent) {
        this.parent = parent;
    }

    @Override
    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.get(protectionLevel);
    }

    @Override
    public void setProtectionLevel(final ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel.getCode();
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    @Override
    public AlertType getAlertType() {
        return AlertType.get(this.alertType);
    }

    @Override
    public void setAlertType(final AlertType alertType) {
        this.alertType = alertType.getCode();
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }



    @SuppressWarnings("MethodWithMultipleReturnPoints")
    @Override
    public int compareTo(final Entity that) {
        final int type = EntityType.get(this.entityType).getOrder().compareTo(that.getEntityType().getOrder());

            return type == 0 ? this.name.compareTo(that.getName().getValue()) : type;

    }

    @Override
    public boolean isOwner(final User user) {
        if (user != null && this.owner.equals(user.getKey())) {
            return true;
        }
        if (user != null && user.getAccessKeys() != null) {
            for (AccessKey key : user.getAccessKeys()) {
                if (key.getAuthLevel().equals(AuthLevel.admin)) {
                    return true;
                }
            }

        }
        return false;

    }

    @Override
    public Date getDateCreated() {

        return new Date(dateCreated.getTime());
    }

    @Override
    public void validate(User user)  {
        if (Utils.isEmptyString(this.owner) || Utils.isEmptyString(this.name) || Utils.isEmptyString(this.parent)) {
            throw new IllegalArgumentException("Entity was missing required data, validation failed"
                    + "owner=" + owner + "name=" + this.name + "parent:" + this.parent);
        }

        if (Utils.isEmptyString(this.owner)) {
            throw new IllegalArgumentException("Owner must not be null");
        }
        if (! this.owner.equals(user.getKey()) &&   this.entityType != EntityType.user.getCode()) {
            throw new IllegalArgumentException("You can't create an entity with an owner other than yourself!");
        }
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        if (dateCreated != null) {
        this.dateCreated = new Date(dateCreated.getTime());
        }
        else {



            this.dateCreated = null;
        }
    }

    @Override
    public boolean entityIsReadable(final User user) {



        boolean retVal = this.getEntityType().equals(EntityType.user) ||
                isOwner(user) ||
                this.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                this.getProtectionLevel().equals(ProtectionLevel.onlyConnection);


        if (this.getEntityType().equals(EntityType.summary) && user == null) {
            retVal = true; //this is a system request from the summary cron job.
        }
        if (this.getEntityType().equals(EntityType.accessKey)) {

        }
        return retVal;


    }




    @SuppressWarnings({"NonFinalFieldReferenceInEquals", "CastToConcreteClass"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityModel)) return false;

        EntityModel that = (EntityModel) o;

        if (alertType != that.alertType) return false;
        if (entityType != that.entityType) return false;
        if (protectionLevel != that.protectionLevel) return false;
        if (readOnly != that.readOnly) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + entityType;
        result = 31 * result + protectionLevel;
        result = 31 * result + alertType;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        return result;
    }

    @Override()
    public String toString() {
        return "EntityModel{" +
                ", readOnly=" + readOnly +
                ", owner='" + owner + '\'' +
                ", parent='" + parent + '\'' +
                ", alertType=" + alertType +
                ", protectionLevel=" + protectionLevel +
                ", entityType=" + entityType +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
