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

package com.nimbits.client.model;


import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.point.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GxtPointCategoryModel extends BaseTreeModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Point> points;

    public void setDiagrams(List<Diagram> diagrams) {
        this.diagrams = diagrams;
    }

    public List<Diagram> getDiagrams() {
        return diagrams;
    }

    private List<Diagram> diagrams;
    private boolean isSystem;
    private Long userFK;
    private CategoryName name;
    private Long id;


    public GxtPointCategoryModel() {

    }

    public GxtPointCategoryModel(Category c, ClientType clientType) {
        this.id = c.getId();
        this.userFK = c.getUserFK();
        this.name = c.getName();

        this.points = c.getPoints();
        if (clientType.equals(ClientType.android)) {
            set(Const.PARAM_NAME, "<A href = \"report.html?client=" + clientType.name() + "&uuid=" + c.getUUID() + "\">" + this.name.getValue() + "</a>");
        } else {
            set(Const.PARAM_NAME, this.name.getValue());
        }
        set(Const.PARAM_ICON, Const.PARAM_CATEGORY);
        set(Const.PARAM_TYPE, Const.PARAM_FOLDER);
        set(Const.PARAM_ENTITY_TYPE, c.getEntityType().getCode());

    }

    public GxtPointCategoryModel(CategoryName name) {
        this.name = name;

    }


    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }


    public List<Point> getPoints() {
        return points;
    }


    public void addPoint(Point p) {
        if (points == null) {
            points = new ArrayList<Point>();

        }
        points.add(p);
    }


    public void setPoints(List<Point> p) {
        this.points = p;
    }

    public Long getId() {
        return id;
    }

    public Long getUserFK() {
        return userFK;
    }

    public void setUserFK(Long userFK) {
        this.userFK = userFK;
    }

    public CategoryName getName() {
        return this.name;
    }

    void setName(CategoryName name) {
        this.name = name;
    }

}
