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

package com.nimbits.client.model.diagram;


import com.nimbits.client.enums.*;
import com.nimbits.client.model.common.*;

import java.io.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 4:07 PM
 */
public class DiagramModel implements Serializable, Diagram {

    private boolean fullScreenView;
    private boolean readOnly;
    private long id;
    private long categoryFk;
    private int protectionLevel;
    private String uuid;
    private long userFk;
    private String blobKey;
    private String name;

    private static long serialVersionUID = 10l;

    public int entityType = EntityType.diagram.getCode();

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    private ClientType clientType;


    public DiagramModel(final long ownerFk) {
        this.userFk = ownerFk;
    }

    public boolean isFullScreenView() {
        return fullScreenView;
    }

    public void setFullScreenView(final boolean fullScreenView) {
        this.fullScreenView = fullScreenView;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public void setProtectionLevel(final int protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public DiagramModel() {
    }

    public DiagramModel(final long userFk, final String blogKey, final String name) {
        this.userFk = userFk;
        this.blobKey = blogKey;
        this.name = name;
        this.protectionLevel = 0;
    }

    public DiagramModel(final Diagram diagram) {
        this.userFk = diagram.getUserFk();
        this.blobKey = diagram.getBlobKey();
        this.name = diagram.getName().getValue();
        this.categoryFk = diagram.getCategoryFk();
        this.id = diagram.getId();
        this.protectionLevel = diagram.getProtectionLevel();
        this.uuid = diagram.getUuid();
        this.readOnly = diagram.isReadOnly();

    }


    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public long getUserFk() {
        return this.userFk;
    }

    @Override
    public String getBlobKey() {
        return blobKey;
    }

    @Override
    public DiagramName getName() {
        return CommonFactoryLocator.getInstance().createDiagramName(name);
    }

    @Override
    public long getCategoryFk() {
        return categoryFk;
    }

    @Override
    public void setCategoryFk(final long id) {
        this.categoryFk = id;
    }


    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public int getProtectionLevel() {
        return this.protectionLevel;

    }

    @Override
    public EntityType getEntityType() {
        return EntityType.get(this.entityType);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }
}
