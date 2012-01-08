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

package com.nimbits.client.model.category.impl;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.point.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CategoryModelImpl implements Serializable, Category {

    private static long serialVersionUID = 9l;

    private int entityType = EntityType.category.getCode();

    private List<Diagram> diagrams;
    private List<Point> points;
    private String name;
    private String description;

    private long userFK;
    private int protectionLevel;
    private String uuid;
    private boolean readOnly;
    private String host;

    public CategoryModelImpl() {

    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public ProtectionLevel getProtectionLevel() {
        return ProtectionLevel.get(this.protectionLevel);
    }

    @Override
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

    private long id;


    public List<Diagram> getDiagrams() {
        return diagrams == null ? new ArrayList<Diagram>() : diagrams;
    }

    public void setDiagrams(final List<Diagram> diagrams) {
        this.diagrams = diagrams;
    }


    public CategoryModelImpl(final Category c) {
        this.id = c.getId();
        this.userFK = c.getUserFK();
        this.name = c.getName().getValue();
        this.points = c.getPoints();
        this.diagrams = c.getDiagrams();
        this.description = c.getDescription();
        this.protectionLevel = c.getProtectionLevel().getCode();
        this.readOnly = c.isReadOnly();
        this.uuid = c.getUUID();
    }

    public CategoryModelImpl(final CategoryName name) {
        this.name = name.getValue();
    }

    @Override
    public void setPoints(final List<Point> p) {
        this.points = p;
    }

    public long getId() {
        return id;
    }

    public long getUserFK() {
        return userFK;
    }

    public void setUserFK(final long userFK) {
        this.userFK = userFK;
    }

    public CategoryName getName() {
        return CommonFactoryLocator.getInstance().createCategoryName(this.name);
    }

    public void setName(final CategoryName name) {
        this.name = name.getValue();
    }

    @Override
    public List<Point> getPoints() {
        return points == null ? new ArrayList<Point>() : points;
    }

    @Override
    public void addPoint(final Point p) {
        if (points == null) {
            points = new ArrayList<Point>();
        }
        points.add(p);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
