/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
                            return (Icons.INSTANCE.point_idle());
                        case HighAlert:
                            return (Icons.INSTANCE.point_high());
                        case LowAlert:
                            return (Icons.INSTANCE.point_low());
                        default:
                            return (Icons.INSTANCE.point_ok());
                    }

                case user:
                    return (Icons.INSTANCE.web());
                case category:
                    return (Icons.INSTANCE.category());
                case subscription:
                    return (Icons.INSTANCE.plugin());
                case calculation:
                    return (Icons.INSTANCE.formula());
                case summary:
                    return (Icons.INSTANCE.summary());

                case sync:
                    return (Icons.INSTANCE.connection());
                case socket:
                    return (Icons.INSTANCE.socket());
                case connection:
                    return (Icons.INSTANCE.connection());
                case instance:
                    return (Icons.INSTANCE.connection());
                case schedule:
                    return (Icons.INSTANCE.schedule());
                case webhook:
                    return (Icons.INSTANCE.webhook());
                default:
                    return (Icons.INSTANCE.point_ok());

            }
        }

    }
}
