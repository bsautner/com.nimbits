package com.nimbits.client.model.entity;

import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;

import java.io.Serializable;
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
    private String entity;
    private String parent;
    private String owner;
    private boolean readOnly = false;
    private String blobKey;
    private String uuid;
    private static final long serialVersionUID =3455345353L;
    private List<Point> children;

    public EntityModel(final CommonIdentifier name,
                       final String description,
                       final EntityType entityType,
                       final ProtectionLevel protectionLevel,
                       final String parent,
                       final String owner,
                       final String blobKey,
                       final String uuid) {
        this.name = name.getValue();
        this.description = description;
        this.entityType = entityType.getCode();
        this.parent = parent;
        this.owner = owner;
        this.protectionLevel = protectionLevel.getCode();
        this.alertType = AlertType.OK.getCode();
        this.blobKey = blobKey;
        this.uuid = uuid;

    }
    public EntityModel() {
    }
    public EntityModel(final Entity anEntity) throws NimbitsException {
     if (anEntity != null) {
        this.key = anEntity.getKey();
        this.name = anEntity.getName().getValue();
        this.description = anEntity.getDescription();
        this.entityType = anEntity.getEntityType().getCode();
        this.entity =anEntity.getKey();
        this.parent = anEntity.getParent();
        this.owner = anEntity.getOwner();
        this.protectionLevel = anEntity.getProtectionLevel().getCode();
        this.alertType = anEntity.getAlertType().getCode();
        this.blobKey = anEntity.getBlobKey();
        this.uuid = anEntity.getUUID();
    }
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
    public void update(Entity update) throws NimbitsException {
        this.description = update.getDescription();
        this.name = update.getName().getValue();
        this.protectionLevel = update.getProtectionLevel().getCode();
        this.parent = update.getParent();
        this.blobKey = update.getBlobKey();
        this.uuid = update.getUUID();
    }


    @Override
    public EntityName getName() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createName(name, EntityType.get(entityType));
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
        return this.entity;
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


    @Override
    public String getBlobKey() {
        return blobKey;
    }
    @Override
    public void setBlobKey(final String blobKey) {
        this.blobKey = blobKey;
    }

    @SuppressWarnings("MethodWithMultipleReturnPoints")
    @Override
    public int compareTo(final Entity that) {
        final int type = EntityType.get(this.entityType).getOrder().compareTo(that.getEntityType().getOrder());
        try {
            return type == 0 ? this.name.compareTo(that.getName().getValue()) : type;
        } catch (NimbitsException e) {
            return -1;
        }
    }

    @Override
    public boolean isOwner(final User user) {
        return user != null && (user.getAuthLevel().equals(AuthLevel.admin) || this.owner.equals(user.getKey()));
    }

    @Override
    public boolean entityIsReadable(final User user) {



        boolean retVal = this.getEntityType().equals(EntityType.user) ||
                isOwner(user) ||
                this.getProtectionLevel().equals(ProtectionLevel.everyone) ||
                this.getProtectionLevel().equals(ProtectionLevel.onlyConnection);

        if (this.getEntityType().equals(EntityType.userConnection) && !this.owner.equals(user.getKey())) {
            retVal = false;
        }
        if (this.getEntityType().equals(EntityType.summary) && user == null) {
            retVal = true; //this is a system request from the summary cron job.
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
        if (blobKey != null ? !blobKey.equals(that.blobKey) : that.blobKey != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
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
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        result = 31 * result + (blobKey != null ? blobKey.hashCode() : 0);
        return result;
    }

    @Override()
    public String toString() {
        return "EntityModel{" +
                "blobKey='" + blobKey + '\'' +
                ", readOnly=" + readOnly +
                ", owner='" + owner + '\'' +
                ", parent='" + parent + '\'' +
                ", entity='" + entity + '\'' +
                ", alertType=" + alertType +
                ", protectionLevel=" + protectionLevel +
                ", entityType=" + entityType +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
