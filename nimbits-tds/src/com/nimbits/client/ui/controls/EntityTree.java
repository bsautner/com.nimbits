/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.ui.icons.Icons;

import java.util.Arrays;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/3/12
 * Time: 4:08 PM
 */
public class EntityTree<ModelData extends com.extjs.gxt.ui.client.data.ModelData> extends EditorTreeGrid<ModelData> {

    public EntityTree() {


        super(new TreeStore<com.extjs.gxt.ui.client.data.ModelData>(), new ColumnModel(
                Arrays.asList(
                        ColumnConfigs.pointNameColumn(),
                        ColumnConfigs.noteColumn(),
                        ColumnConfigs.timestampColumn(),
                        ColumnConfigs.dataColumn())
        ));

        setIconProvider(new ModelDataModelIconProvider<ModelData>());
    }

    @Override
    protected boolean hasChildren(com.extjs.gxt.ui.client.data.ModelData model) {
         return true;
    }

    private static class ModelDataModelIconProvider<ModelData extends com.extjs.gxt.ui.client.data.ModelData> implements ModelIconProvider<ModelData> {
        ModelDataModelIconProvider() {
        }

        @Override
        public AbstractImagePrototype getIcon(com.extjs.gxt.ui.client.data.ModelData model) {

            switch (((TreeModel) model).getEntityType()) {
                case point:
                    switch (((TreeModel) model).getAlertType()) {
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

                case subscription:
                    return AbstractImagePrototype.create(Icons.INSTANCE.plugin());
                case calculation:
                    return AbstractImagePrototype.create(Icons.INSTANCE.formula());
                case intelligence:
                    return AbstractImagePrototype.create(Icons.INSTANCE.connect());
                case feed:
                    return AbstractImagePrototype.create(Icons.INSTANCE.radial());
                case resource:
                    return AbstractImagePrototype.create(Icons.INSTANCE.filter());
                case summary:
                    return AbstractImagePrototype.create(Icons.INSTANCE.summary());
                case accessKey:
                    return AbstractImagePrototype.create(Icons.INSTANCE.key());
                default:
                    return AbstractImagePrototype.create(Icons.INSTANCE.point_ok());

            }
        }

    }
}
