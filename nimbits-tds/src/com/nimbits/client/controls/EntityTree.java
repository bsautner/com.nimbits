package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.treegrid.*;
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


    }

    @Override
    protected boolean hasChildren(com.extjs.gxt.ui.client.data.ModelData model) {
//        final String entityTypeVal = model.get(Const.PARAM_ENTITY_TYPE);
//
//        final EntityType entityType = EntityType.get (Integer.valueOf(entityTypeVal));
//
//        return entityType.equals(EntityType.category) ||  super.hasChildren((ModelData) model);

        return model instanceof GxtPointCategoryModel ||
                !(model instanceof GxtPointModel)
                        && super.hasChildren((ModelData) model);
    }
}
