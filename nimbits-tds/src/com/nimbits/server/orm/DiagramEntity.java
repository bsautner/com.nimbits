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

package com.nimbits.server.orm;


import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;

import javax.jdo.annotations.*;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:59 PM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class DiagramEntity  {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private long userFk;

    @Persistent
    private long categoryFk;

    @Persistent
    private BlobKey blobKey;

    public void setBlobKey(BlobKey blobKey) {
        this.blobKey = blobKey;
    }

    @Persistent
    private String name;

    @Persistent
    private String uuid;

    @NotPersistent
    private int entityType = EntityType.file.getCode();


    public void setName(EntityName name) {
        this.name = name.getValue();
    }

    public void setProtectionLevel(int protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public int getProtectionLevel() {
        if (protectionLevel == null) {
            protectionLevel = 0;
        }
        return protectionLevel;
    }

    public boolean isReadOnly() {
        return readOnly;
    }


    public boolean isFullScreenView() {
        return fullScreenView;
    }


    public void setFullScreenView(boolean fullScreenView) {
        this.fullScreenView = fullScreenView;
    }


    public void setClientType(ClientType clientType) {

    }


    public ClientType getClientType() {
        return null;
    }


    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }

    public Date getDateCreated() {
        return dateCreated;
    }


    public String getUuid() {
        return uuid;
    }

    @Persistent
    private Integer protectionLevel;

    @Persistent
    private Date dateCreated;

    @NotPersistent
    private boolean fullScreenView;

    @NotPersistent
    private boolean readOnly;

    public DiagramEntity(long userFk, BlobKey blobKey, EntityName name, long categoryFk) {
        this.userFk = userFk;
        this.blobKey = blobKey;
        this.name = name.getValue();
        this.uuid = UUID.randomUUID().toString();
        this.categoryFk = categoryFk;
        this.dateCreated = new Date();
        this.protectionLevel = 0;
    }


    public DiagramEntity() {
    }



    public long getId() {

        return this.id != null ? this.id : 0;

    }

    public long getCategoryFk() {
        return this.categoryFk;
    }


    public void setCategoryFk(long id) {
        this.categoryFk = id;
    }


    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }


    public long getUserFk() {
        return this.userFk;
    }


    public String getBlobKey() {
        return blobKey.getKeyString();
    }


    public EntityName getName() {
        return CommonFactoryLocator.getInstance().createName(this.name);
    }

}
