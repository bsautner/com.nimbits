package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.GxtDiagramModel;
import com.nimbits.client.model.GxtPointCategoryModel;
import com.nimbits.client.model.GxtPointModel;

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


                if (model instanceof GxtPointCategoryModel) {
                    return AbstractImagePrototype.create(Icons.INSTANCE.category());
                } else if (model instanceof GxtPointModel) {
                    switch (((GxtPointModel) model).getAlertState()) {
                        case IdleAlert:
                            return AbstractImagePrototype.create(Icons.INSTANCE.point_idle());
                        case HighAlert:
                            return AbstractImagePrototype.create(Icons.INSTANCE.point_high());
                        case LowAlert:
                            return AbstractImagePrototype.create(Icons.INSTANCE.point_low());
                        default:
                            return AbstractImagePrototype.create(Icons.INSTANCE.point_ok());
                    }
                } else if (model instanceof GxtDiagramModel) {
                    return AbstractImagePrototype.create(Icons.INSTANCE.diagram());
                } else {
                    return null;
                }
                // return IconHelper.createStyle((String)
                // model.getInstance("icon"));
                // } else {
                ////     return null;
                // }
            }
        });

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
