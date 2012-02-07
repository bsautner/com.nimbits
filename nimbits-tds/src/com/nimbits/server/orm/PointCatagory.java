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

import com.nimbits.client.enums.*;
import com.nimbits.client.model.category.Category;

import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;

import javax.jdo.annotations.*;
import java.util.ArrayList;
import java.util.List;


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class PointCatagory implements Category {

    private static final long serialVersionUID = 5L;

    public PointCatagory(final EntityName name) {
        this.name = name.getValue();
    }

    public PointCatagory() {

    }

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    public List<Diagram> getDiagrams() {
        return diagrams;
    }

    @Override
    public String getDescription() {
        return description == null ? "" : description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @NotPersistent
    private List<Point> _points;

    @NotPersistent
    private List<Diagram> diagrams;

    @Persistent
    private Long userFK;

    @Persistent
    private String name;

    @Persistent
    private String description;

    @Persistent
    private String uuid;

    @Persistent
    private Integer protectionLevel;

    @NotPersistent
    Boolean isReadOnly;

    @NotPersistent
    String host;

    @NotPersistent
    private int entityType = EntityType.category.getCode();

    @Override
    public List<Point> getPoints() {
        return _points == null ? new ArrayList<Point>() : _points;
    }

    @Override
    public void setDiagrams(final List<Diagram> diagrams) {
        this.diagrams = diagrams;
    }

    @Override
    public void setPoints(final List<Point> p) {
        this._points = p;
    }

    @Override
    public void addPoint(final Point p) {
        if (_points == null) {
            _points = new ArrayList<Point>();

        }
        p.setCatID(this.getId());

        _points.add(p);

    }

    @Override
    public long getId() {
        return id == null ? 0 : id;
    }

    @Override
    public long getUserFK() {

        return userFK == null ? 0 : userFK;
    }

    @Override
    public void setUserFK(final long userFK) {
        this.userFK = userFK;
    }

    @Override
    public EntityName getName() {
        return CommonFactoryLocator.getInstance().createName(this.name);
    }

    @Override
    public void setName(final EntityName name) {
        this.name = name.getValue();
    }


    @Override
    public boolean isReadOnly() {
        return this.isReadOnly == null ? false : this.isReadOnly;
    }

    @Override
    public ProtectionLevel getProtectionLevel() {
        return this.protectionLevel == null ? ProtectionLevel.onlyMe : ProtectionLevel.get(this.protectionLevel);
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel.getCode();
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public EntityType getEntityType() {
      return EntityType.get(entityType);
    }


}
