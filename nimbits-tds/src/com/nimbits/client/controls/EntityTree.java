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

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.treegrid.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/3/12
 * Time: 4:08 PM
 */
public class EntityTree<ModelData extends com.extjs.gxt.ui.client.data.ModelData> extends EditorTreeGrid<ModelData> {

    public EntityTree(TreeStore store, ColumnModel cm) {
        super(store, cm);

        setIconProvider( new ModelIconProvider<ModelData>() {
            @Override
            public AbstractImagePrototype getIcon(com.extjs.gxt.ui.client.data.ModelData model) {
                // if (model.getInstance("icon") != null) {

                EntityType type = ((GxtModel) model).getEntityType();
              //  AlertType alert = ((GxtModel) model).getAlertType();
                switch (type) {

                    case point:
                        switch (((GxtModel) model).getAlertType()) {
                            case IdleAlert:
                                return AbstractImagePrototype.create(Icons.INSTANCE.point_idle());
                            case HighAlert:
                                return AbstractImagePrototype.create(Icons.INSTANCE.point_high());
                            case LowAlert:
                                return AbstractImagePrototype.create(Icons.INSTANCE.point_low());
                            default:
                                return AbstractImagePrototype.create(Icons.INSTANCE.point_ok());
                        }

                    case user:
                        return AbstractImagePrototype.create(Icons.INSTANCE.web());

                    case category:
                        return AbstractImagePrototype.create(Icons.INSTANCE.category());
                     case userConnection:
                        return AbstractImagePrototype.create(Icons.INSTANCE.connection());
                    case file:
                        return AbstractImagePrototype.create(Icons.INSTANCE.diagram());
                    case subscription:
                        return AbstractImagePrototype.create(Icons.INSTANCE.plugin());
                    case calculation:
                        return AbstractImagePrototype.create(Icons.INSTANCE.formula());
                    case intelligence:
                        return AbstractImagePrototype.create(Icons.INSTANCE.connect());
                    default:
                        return AbstractImagePrototype.create(Icons.INSTANCE.point_ok());

                }
            }

        });
    }
//
//    @Override
//    protected boolean hasChildren(com.extjs.gxt.ui.client.data.ModelData model) {
////        final String entityTypeVal = model.get(Const.PARAM_ENTITY_TYPE);
////
////        final EntityType entityType = EntityType.get (Integer.valueOf(entityTypeVal));
////
////        return entityType.equals(EntityType.category) ||  super.hasChildren((ModelData) model);
//
//        return  true;(model instanceof GxtModel) ||
//                (!(model instanceof GxtModel)
//                        && super.hasChildren((ModelData) model));
//    }


}
