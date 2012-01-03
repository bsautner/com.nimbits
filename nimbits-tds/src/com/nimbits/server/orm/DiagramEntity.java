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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;


import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.diagram.DiagramName;
import com.nimbits.client.model.user.User;

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
public class DiagramEntity implements Diagram {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private long userFk;

    @Persistent
    private long categoryFk;

    public void setBlobKey(BlobKey blobKey) {
        this.blobKey = blobKey;
    }

    @Persistent
    private BlobKey blobKey;

    @Persistent
    private String name;

    @Persistent
    private String uuid;

    public void setName(DiagramName name) {
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

    @Override
    public boolean isFullScreenView() {
        return fullScreenView;
    }

    @Override
    public void setFullScreenView(boolean fullScreenView) {
        this.fullScreenView = fullScreenView;
    }

    @Override
    public void setClientType(ClientType clientType) {

    }

    @Override
    public ClientType getClientType() {
        return null;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
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

    public DiagramEntity(long userFk, BlobKey blobKey, DiagramName name, long categoryFk) {
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

    public DiagramEntity(User u, BlobKey blobKey, DiagramName name, Category category) {
        this.userFk = u.getId();
        this.blobKey = blobKey;
        this.name = name.getValue();
        this.categoryFk = category.getId();
        this.uuid = UUID.randomUUID().toString();
        this.dateCreated = new Date();
        this.protectionLevel = 0;
    }

    @Override
    public long getId() {

        return this.id != null ? this.id : 0;

    }

    public long getCategoryFk() {
        return this.categoryFk;
    }

    @Override
    public void setCategoryFk(long id) {
        this.categoryFk = id;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public long getUserFk() {
        return this.userFk;
    }

    @Override
    public String getBlobKey() {
        return blobKey.getKeyString();
    }

    @Override
    public DiagramName getName() {
        return CommonFactoryLocator.getInstance().createDiagramName(this.name);
    }

}
