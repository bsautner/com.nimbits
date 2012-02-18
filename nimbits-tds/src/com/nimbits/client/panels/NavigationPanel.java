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

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.Style.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.dnd.DND.*;
import com.extjs.gxt.ui.client.dnd.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.controls.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.recordedvalues.*;
import com.nimbits.shared.*;

import java.util.*;


class NavigationPanel extends NavigationEventProvider {

    private final ContentPanel mainPanel;
    private EntityTree<ModelData> tree;
    private TreeStore<ModelData> store;
    private Timer updater;
    private boolean expanded = false;
    private Map<String, String> settings;
    List<String> parents;
    EntityContextMenu context;
    private final User user;

    public NavigationPanel(final User user,
                           final ClientType clientType,
                           final Map<String, String> settings) {

        this.settings = settings;
       // this.clientType = clientType;
        this.user = user;
        mainPanel = new ContentPanel();
        mainPanel.setHeaderVisible(false);
        mainPanel.setFrame(false);
        mainPanel.setBodyBorder(false);
        mainPanel.setScrollMode(Scroll.AUTOY);
        mainPanel.setLayout(new FillLayout());

        add(mainPanel);
        NavigationToolBar toolBar = createToolbar(settings);

        mainPanel.setTopComponent(toolBar);
        setBorders(false);
        setScrollMode(Scroll.NONE);
        getUserEntities(false);


    }

    private NavigationToolBar createToolbar(Map<String, String> settings) {
        NavigationToolBar toolBar = new NavigationToolBar(tree, settings);

        toolBar.addEntityModifiedListeners(new NavigationToolBar.EntityModifiedListener() {
            @Override
            public void onEntityModified(GxtModel model, Action action) {
                switch (action) {
                    case update: case create:
                        addUpdateTreeModel(model, false);
                        break;
                    case refresh:
                        getUserEntities(true);
                        break;
                }
            }
        });

        toolBar.addExpandListeners(new NavigationToolBar.ExpandListener() {
            @Override
            public void onExpand() {
                if (!expanded) {
                    tree.expandAll();
                    expanded=true;
                }
                else {
                    tree.collapseAll();
                    expanded=false;
                }
            }
        });
        return toolBar;
    }

    private void addEntity(final Entity entity) {
        if (! Utils.isEmptyString(entity.getParent()) && ! parents.contains(entity.getParent())) {
            parents.add(entity.getParent());
        }

    }

    private void createTree(final List<Entity> result) {

        store = new TreeStore<ModelData>();
        ColumnConfigs columnConfigs = new ColumnConfigs();
        ColumnModel cm = new ColumnModel(
                Arrays.asList(
                        columnConfigs.pointNameColumn(true),
                        columnConfigs.currentValueColumn())
        );
        tree = new EntityTree<ModelData>(store, cm);

        final TreeGridDropTarget target = new TreeGridDropTarget(tree);
        target.setAllowSelfAsSource(true);
        target.setFeedback(Feedback.BOTH);
        tree.addListener(Events.AfterEdit, afterEditListener);
        treePropertyBuilder();
        treeStoreBuilder(result);
        treeDNDBuilder();

        mainPanel.removeAll();
        mainPanel.add(tree);


    }

    private void updateModel(Value value, GxtModel model) {
        model.set(Const.PARAM_VALUE, value.getNumberValue());
        model.set(Const.PARAM_DATA, value.getData());
        model.set(Const.PARAM_TIMESTAMP, value.getTimestamp());
        model.set(Const.PARAM_NOTE, value.getNote());
        model.setAlertType(value.getAlertState());
        model.setDirty(false);
        store.update(model);
        notifyValueEnteredListener(model.getBaseEntity(), value);
    }

    private void treePropertyBuilder() {
       context = new EntityContextMenu(tree, settings);
        context.addEntityModifiedListeners(new EntityContextMenu.EntityModifiedListener() {
            @Override
            public void onEntityModified(GxtModel model, Action action) {
                switch (action) {
                    case delete: {
                        removeEntity(model);
                        break;
                    }
                    case update: case create:
                        addUpdateTreeModel(model, false);
                        break;
                }
            }
        });
        tree.setContextMenu(context);
        tree.setStateful(true);
        tree.setClicksToEdit(EditorGrid.ClicksToEdit.ONE);
        tree.setTrackMouseOver(true);
        tree.getView().setAutoFill(true);
        tree.addListener(Events.RowDoubleClick, treeDoubleClickListener);
    }

    private void treeDNDBuilder() {
        TreeGridDragSource source = new TreeGridDragSource(tree);
        source.addDNDListener(new DNDListener() {
            ModelData selectedModel;

            @Override
            public void dragStart(DNDEvent e) {
                super.dragStart(e);
                selectedModel = tree.getSelectionModel().getSelectedItem();
                GxtModel gxtModel = (GxtModel)selectedModel;

                e.setCancelled(  gxtModel.isReadOnly());
                e.getStatus().setStatus(  ! gxtModel.isReadOnly());

            }

            @Override
            public void dragDrop(final DNDEvent e) {
                super.dragDrop(e);
                if (!(e.getTarget().getInnerHTML().equals("&nbsp;"))) {
                    if (selectedModel instanceof GxtModel) {
                        final GxtModel model = (GxtModel) selectedModel;
                        selectedModel.set(Const.PARAM_NAME, model.getName().getValue());
                        final Entity draggedEntity =  model.getBaseEntity();
                        final Entity target = getDropTarget(e.getTarget().getInnerText());
                        e.setCancelled(  target.isReadOnly());
                        e.getStatus().setStatus(  ! target.isReadOnly());


                        if (! model.isReadOnly() && ! target.isReadOnly()){
                            moveEntity(draggedEntity, target);
                        }


                    }

                }
            }
        });
    }

    private Entity getDropTarget(String targetName) {

        ModelData modelData = tree.getTreeStore().findModel(Const.PARAM_NAME, targetName);
        return ((GxtModel) modelData).getBaseEntity();


    }

    private void moveEntity(Entity draggedEntity, Entity target) {
        EntityServiceAsync service = GWT.create(EntityService.class);
        draggedEntity.setParent(target.getEntity());

        service.addUpdateEntity(draggedEntity, new AsyncCallback<Entity>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(Entity result) {

            }
        });
    }

    private void addChildrenToModel(final List<Entity> result, List<String> parents, GxtModel model) {


        for (final Entity entity : result) {
            if (! entity.getEntityType().equals(EntityType.user)) {// entity.getEntity().equals(this.user.getUuid()) ) {
                if (entity.getParent().equals(model.getUUID())) {
                    GxtModel model2 = new GxtModel(entity);
                    if (parents.contains(entity.getEntity()) && ! entity.getEntityType().equals(EntityType.user)) {
                        addChildrenToModel(result, parents, model2);
                    }

                    model.add(model2);
                }
            }
        }
        // return model;
    }

    private void treeStoreBuilder(final List<Entity> result) {

        final List<ModelData> model = new ArrayList<ModelData>();
        parents = new ArrayList<String>();
        for (final Entity entity : result) {
            addEntity(entity);
        }
        // addEntity(entity);
        final GxtModel userModel = new GxtModel(user);
        addChildrenToModel(result, parents, userModel);
        model.add(userModel);

        store.add(model, true);
        tree.expandAll();

    }

    public void addUpdateTreeModel(final GxtModel model, final boolean refresh) {

        if (tree != null && tree.getStore() != null) {

            store = tree.getTreeStore();
            final ModelData mx = store.findModel(Const.PARAM_ID, model.getBaseEntity().getEntity());
            if (mx != null) {
                final GxtModel m = (GxtModel)mx;
                m.update(model.getBaseEntity());
                store.update(m);
                if (! refresh) {
                    tree.setExpanded(mx, true);
                }
            }
            else {
                final ModelData parent = store.findModel(Const.PARAM_ID, model.getBaseEntity().getParent());
                if (parent != null) {
                    store.add(parent, model, true);

                }
            }
            tree.setExpanded(model, true);
        }
    }

    private void removeEntity(GxtModel currentModel) {

        if (tree != null && tree.getStore() != null) {

            store = tree.getTreeStore();
            GxtModel m = (GxtModel) tree.getTreeStore().findModel(Const.PARAM_ID, currentModel.getBaseEntity().getEntity());
            store.remove(m);

        }
    }

    @Override
    protected void afterRender() {
        super.afterRender();
        layout(true);
    }

    @Override
    protected void onAttach() {
        updater = new Timer() {
            @Override
            public void run() {
                try {
                    updateValues();
                } catch (NimbitsException e) {
                    GWT.log(e.getMessage(), e);
                }
            }
        };
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    private void updateValues() throws NimbitsException {
        if (tree != null) {
            Map<String, Entity> entityMap = new HashMap<String, Entity>();

            for (ModelData m : tree.getTreeStore().getAllItems()) {
                final GxtModel model = (GxtModel) m;
                if (model.getEntityType().equals(EntityType.point)) {
                    entityMap.put(model.getUUID(), model.getBaseEntity());
                }
            }

            final PointServiceAsync service = GWT.create(PointService.class);
            service.getPoints(entityMap, new AsyncCallback<Map<String, Point>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GWT.log(throwable.getMessage(), throwable);
                }

                @Override
                public void onSuccess(Map<String, Point> stringPointMap) {
                    final TreeStore<ModelData> models = tree.getTreeStore();


                    for (final ModelData m : models.getAllItems()) {

                        final GxtModel model = (GxtModel) m;


                        if (!model.isDirty() && model.getEntityType().equals(EntityType.point)) {

                            if (stringPointMap.containsKey(model.getUUID())) {
                                Point p = stringPointMap.get(model.getUUID());
                                if (p.getValue() == null) {
                                    model.setAlertType(AlertType.OK);
                                    model.setValue(ValueModelFactory.createValueModel(0.0));
                                }
                                else {
                                    model.setAlertType(p.getValue().getAlertState());
                                    model.setValue(p.getValue());
                                }


                            }
                            models.update(m);
                        }

                    }
                }
            });


        }
    }

    //toolbars


    private final Listener<TreeGridEvent<ModelData>> treeDoubleClickListener = new Listener<TreeGridEvent<ModelData>>() {
        @Override
        public void handleEvent(TreeGridEvent<ModelData> be) {
            ModelData mx = tree.getSelectionModel()
                    .getSelectedItem();

            if (mx != null) {
                GxtModel model = ((GxtModel) mx);
                Entity entity =  model.getBaseEntity();

                switch (entity.getEntityType()) {

                    case user:
                        break;
                    case point:
                        addEntityChildren(entity, model);
                        notifyEntityClickedListener(entity);
                        break;
                    case category:
                        addEntityChildren(entity, model);
                        notifyEntityClickedListener(entity);
                        break;
                    case file:
                        notifyEntityClickedListener(entity);
                        break;
                    case subscription:
                        addEntityChildren(entity, model);
                        notifyEntityClickedListener(entity);
                        break;
                    case userConnection:
                        break;
                    case calculation:
                        context.showCalcPanel(entity);
                        break;
                }



            }
        }

    };

    private void addEntityChildren(Entity entity, GxtModel model) {
        if (model.getChildCount() > 0)
            for (int i = 0; i < model.getChildCount(); i++) {
                GxtModel m = (GxtModel) model.getChild(i);
                Entity c =model.getBaseEntity();
                addEntityChildren(c, m);
                entity.addChild(c);

            }
    }

    private final Listener<GridEvent> afterEditListener = new Listener<GridEvent>() {

        @Override
        public void handleEvent(final GridEvent be) {
            final GxtModel model = (GxtModel) be.getModel();
            if (!model.isReadOnly()) {
                model.setDirty(true);


                final Entity entity =model.getBaseEntity();

                final Date timestamp = new Date();
                final Double v = model.get(Const.PARAM_VALUE);

                final Value value = ValueModelFactory.createValueModel(v, timestamp);
                RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
                service.recordValue(entity, value, new AsyncCallback<Value>() {
                    @Override
                    public void onFailure(final Throwable throwable) {
                        be.getRecord().reject(false);
                        updater.cancel();
                    }

                    @Override
                    public void onSuccess(final Value value) {
                        be.getRecord().commit(false);
                        model.setDirty(false);
                        updateModel(value, model);

                    }
                });

            }
        }
    };
    //service calls
    private void getUserEntities(final boolean refresh)  {


        final EntityServiceAsync service = GWT.create(EntityService.class);
        service.getEntities(new AsyncCallback<List<Entity>>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(List<Entity> result) {
                if (refresh) {
                    for (Entity e : result) {
                        addUpdateTreeModel(new GxtModel(e), true);
                    }
                }
                else {
                    createTree(result);
                    doLayout();
                }

            }
        });

    }


}