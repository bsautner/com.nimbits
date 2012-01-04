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

import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.diagram.DiagramName;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/8/11
 * Time: 5:42 PM
 */
public class GxtDiagramModel extends GxtBaseModel {
    private long id;
    private DiagramName name;

    public GxtDiagramModel(Diagram diagram) {
        this.id = diagram.getId();
        this.name = diagram.getName();
        set(Const.PARAM_ID, this.id);
        set(Const.PARAM_NAME, this.name.getValue());
        set(Const.PARAM_ICON, Const.PARAM_DIAGRAM);
        set(Const.PARAM_ENTITY_TYPE, diagram.getEntityType().getCode());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DiagramName getName() {
        return this.name;
    }

    public void setName(DiagramName name) {
        this.name = name;
    }


}
