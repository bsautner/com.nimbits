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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.controls.EntityTree;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.datapoints.PointServiceAsync;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;

import com.nimbits.shared.Utils;

import java.util.*;


class NavigationPanel extends NavigationEventProvider {

    private final ContentPanel mainPanel;
    private EntityTree<ModelData> tree;
    private TreeStore<ModelData> store;
    private Timer updater;
    private boolean expanded = false;
    private ClientType clientType;
    private Map<String, String> settings;
    List<String> parents;
    private GxtModel currentModel;
    private final User user;

    public NavigationPanel(final User user,
                           final ClientType clientType,
                           final Map<String, String> settings) {

        this.settings = settings;
        this.clientType = clientType;
        this.user = user;
        mainPanel = new ContentPanel();
        mainPanel.setHeaderVisible(false);
        mainPanel.setFrame(false);
        mainPanel.setBodyBorder(false);
        mainPanel.setScrollMode(Scroll.AUTOY);
        mainPanel.setLayout(new FillLayout());
        mainPanel.setTopComponent(treeToolBar());
        add(mainPanel);

        setBorders(false);
        setScrollMode(Scroll.NONE);
        getUserEntities(false);


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

        tree.setContextMenu(createContextMenu());
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

    private void createPointPropertyWindow(Entity entity) {
        final Window window = new Window();


        final PointPanel panel = new PointPanel(user, entity);

        panel.addPointUpdatedListeners(new PointPanel.PointUpdatedListener() {
            @Override
            public void onPointUpdated(Entity result) {
                addUpdateTreeModel(result, false);
            }
        });



        window.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connect()));
        window.setSize(466, 520);
        window.setPlain(false);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setHeading(entity.getName().getValue() + " Properties");
        window.setHeaderVisible(true);
        window.setBodyBorder(true);
//	window.setLayout(new FitLayout());
        window.add(panel);
        window.show();
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

    public void addUpdateTreeModel(final Entity result, final boolean refresh) {

        if (tree != null && tree.getStore() != null) {
            final GxtModel model = new GxtModel(result);
            store = tree.getTreeStore();
            final ModelData mx = store.findModel(Const.PARAM_ID, result.getEntity());
            if (mx != null) {
                final GxtModel m = (GxtModel)mx;
                m.update(result);
                store.update(m);
                if (! refresh) {
                    tree.setExpanded(mx, true);
                }
            }
            else {
                final ModelData parent = store.findModel(Const.PARAM_ID, result.getParent());
                if (parent != null) {
                    store.add(parent, model, true);

                }
            }
            tree.setExpanded(model, true);
        }



    }

    private void removeEntity(final Entity result, GxtModel currentModel) {

        if (tree != null && tree.getStore() != null) {
            // GxtModel model = new GxtModel(result);
            store = tree.getTreeStore();
            store.remove(currentModel);

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

    private Button addNewPointButton() {
        final Button newPoint = new Button("");
        newPoint.setText("New Data Point");
        newPoint.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.addNew()));
        newPoint.setToolTip(Const.MESSAGE_NEW_POINT);
        newPoint.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                final MessageBox box = MessageBox.prompt(
                        Const.MESSAGE_NEW_POINT,
                        Const.MESSAGE_NEW_POINT_PROMPT);
                box.addCallback(createNewPointListener);
            }
        });
        return newPoint;
    }

    private Button expandAllButton() {
        final Button button = new Button("");
        // newPoint.setText("New Data Point");
        button.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.expand()));
        button.setToolTip("expand all");
        button.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (!expanded) {
                    tree.expandAll();
                    expanded=true;
                    button.setToolTip("expand all");
                }
                else {
                    button.setToolTip("collapse all");
                    tree.collapseAll();
                    expanded=false;
                }

            }
        });
        return button;
    }

    private ToolBar treeToolBar() {
        final ToolBar toolBar = new ToolBar();
        toolBar.add(addNewPointButton());

        toolBar.add(new SeparatorToolItem());

        if (! clientType.equals(ClientType.android)) {
            toolBar.add(addNewFileButton());
            toolBar.add(addNewCategoryButton());
        }
        toolBar.add(expandAllButton());

        return toolBar;
    }

    private Button addNewFileButton() {
        Button newDiagram = new Button();
        newDiagram.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.diagram()));
        newDiagram.setToolTip("Upload a file");

        newDiagram.addListener(Events.OnClick, addFileListener);
        return newDiagram;
    }

    private Button addNewCategoryButton() {
        final Button newCategory = new Button();
        newCategory.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.category()));
        newCategory.setToolTip(Const.MESSAGE_ADD_CATEGORY);
        newCategory.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent be) {
                final MessageBox box = MessageBox.prompt(Const.MESSAGE_NEW_CATEGORY,
                        Const.MESSAGE_NEW_CATEGORY_PROMPT);
                box.addCallback(new Listener<MessageBoxEvent>() {
                    @Override
                    public void handleEvent(final MessageBoxEvent be) {
                        final String newEntityName = be.getValue();
                        final EntityName categoryName = CommonFactoryLocator.getInstance().createName(newEntityName);

                        final EntityServiceAsync service = GWT.create(EntityService.class);
                        Entity entity = EntityModelFactory.createEntity(categoryName, EntityType.category);

                        service.addUpdateEntity(entity,
                                new AsyncCallback<Entity>() {
                                    @Override
                                    public void onFailure(Throwable caught) {
                                        updater.cancel();
                                        Info.display(Const.WORD_ERROR,
                                                caught.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(final Entity c) {
                                        addUpdateTreeModel(c, false);
                                    }
                                });

                    }
                });
            }
        });
        return newCategory;
    }

    private MenuItem deleteContext() {
        MenuItem retObj = new MenuItem();


        retObj.setText("Delete");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.delete()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (GxtModel)selectedModel;
                if (! currentModel.isReadOnly()) {
                    MessageBox.confirm("Confirm", "Are you sure you want delete this? Doing so will permanently delete it including all of it's children (points, documents data etc)"
                            , deleteEntityListener);
                }

            }
        });
        return retObj;
    }

    private MenuItem propertyContext() {
        MenuItem retObj = new MenuItem();

        retObj.setText(Const.WORD_PROPERTIES);
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.edit()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                GxtModel selectedModel = (GxtModel) tree.getSelectionModel().getSelectedItem();
                Entity entity = selectedModel.getBaseEntity();
                switch (selectedModel.getEntityType()) {
                    case category:  {


                        CategoryPropertyPanel dp = new CategoryPropertyPanel(entity);
                        dp.addEntityDeletedListeners(new EntityDeletedListener() {
                            @Override
                            public void onEntityDeleted(Entity entity1 )  {
                                //entityMap.remove(entity1.getEntity());
                            }
                        });

                        final Window w = new Window();
                        w.setWidth(500);
                        w.setHeight(400);
                        w.setHeading(entity.getName().getValue() + " " + Const.WORD_PROPERTIES);
                        w.add(dp);
                        w.show();
                        break;

                    }
                    case point: {

                        createPointPropertyWindow(entity);

                        break;


                    }

                    case subscription: {
                        showSubscriptionPanel(entity);
                        break;
                    }
                    case file: {
                        FilePropertyPanel dp = new FilePropertyPanel(entity);
                        final Window w = new Window();
                        w.setWidth(500);
                        w.setHeight(400);
                        w.setHeading(entity.getName().getValue() + " " + Const.WORD_PROPERTIES);
                        w.add(dp);
                        w.show();
                        break;
                    }
                }
            }
        });
        return retObj;
    }

    public void showSubscriptionPanel(final Entity entity) {
        SubscriptionPanel dp = new SubscriptionPanel(entity, settings);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(500);
        w.setHeight(500);
        w.setHeading("Subscribe");
        w.add(dp);
        dp.addSubscriptionAddedListener(new EntityAddedListener() {
            @Override
            public void onEntityAdded(Entity entity) {
                w.hide();
                Cookies.removeCookie(Action.subscribe.name());
                addUpdateTreeModel(entity, false);
            }
        });

        w.show();
    }

    private Menu createContextMenu() {
        Menu contextMenu = new Menu();
        contextMenu.add(subscribeContext());
      //  contextMenu.add(publishContext());
        contextMenu.add(reportContext());
        contextMenu.add(propertyContext());
        contextMenu.add(copyContext());
        contextMenu.add(deleteContext());
        return contextMenu;
    }

    private MenuItem subscribeContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Subscribe");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.plugin()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (GxtModel) selectedModel;
                Entity entity =  currentModel.getBaseEntity();
                //TODO for now only subscribe to points
                if (entity.getEntityType().equals(EntityType.subscription)  ||
                        entity.getEntityType().equals(EntityType.point)) {
                    showSubscriptionPanel(entity);
                }

            }
        });
        return retObj;
    }

    private MenuItem copyContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Copy");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (GxtModel) selectedModel;

                final MessageBox box;
                if (currentModel.getEntityType().equals(EntityType.point) && ! currentModel.isReadOnly()) {

                    box= MessageBox.prompt(
                            Const.MESSAGE_NEW_POINT,
                            Const.MESSAGE_NEW_POINT_PROMPT);
                    box.addCallback(copyPointListener);
                }
                else {
                    box = MessageBox.alert("Not supported", "Sorry, for the moment you can only copy your data points", null);

                }
                box.show();
            }
        });
        return retObj;
    }

    private MenuItem publishContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Publish");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.publish()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                GxtModel model = (GxtModel) selectedModel;
                publishEntity(model); //TODO handle different types

            }

            private void publishEntity(GxtModel model) {

                Entity p = model.getBaseEntity();
                PointServiceAsync pointService = GWT.create(PointService.class);
                //TODO
//                pointService.publishPoint(p, new AsyncCallback<Point>() {
//
//                    @Override
//                    public void onFailure(Throwable e) {
//                        updater.cancel();
//                        Info.display(Const.WORD_ERROR,
//                                e.getMessage());
//                    }
//
//                    @Override
//                    public void onSuccess(Point point) {
//                        com.google.gwt.user.client.Window.alert("Your data points is now set to public, people can now discover it by" +
//                                " searching for its name or description on nimbits.com. Please select the property option to edit these values.");
//
//                    }
//                });

            }


        });
        return retObj;
    }

    private MenuItem reportContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Report");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.form()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                GxtModel model = (GxtModel) selectedModel;
                if (model.getEntityType().equals(EntityType.point) || model.getEntityType().equals(EntityType.category)) {
                    Entity p =  model.getBaseEntity();
                    openUrl(p.getEntity(), p.getName().getValue());
                }



            }

            private void openUrl(String uuid, String title) {
                String u = com.google.gwt.user.client.Window.Location.getHref()
                        + "?uuid=" + uuid
                        + "&count=10";
                com.google.gwt.user.client.Window.open(u, title, Const.PARAM_DEFAULT_WINDOW_OPTIONS);
            }
        });

        return retObj;
    }

    //listeners
    private final Listener<MessageBoxEvent> copyPointListener  = new Listener<MessageBoxEvent>() {
        private String newEntityName;


        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName);
                Entity entity =  currentModel.getBaseEntity();

                service.copyEntity(entity, name,new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        box.close();
                    }

                    @Override
                    public void onSuccess(Entity entity) {
                        box.close();
                        addUpdateTreeModel(entity, false);
                    }
                });

            }
        }
    };

    private final Listener<MessageBoxEvent> createNewPointListener = new Listener<MessageBoxEvent>() {
        private String newEntityName;

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                PointServiceAsync service = GWT.create(PointService.class);
                EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName);
                Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                service.addPoint(name, new AsyncCallback<Point>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Info.display("Could not create "
                                + newEntityName,
                                caught.getMessage());
                        box.close();
                    }

                    @Override
                    public void onSuccess(Point result) {
                        Entity e = EntityModelFactory.createEntity(user, result);
                        addUpdateTreeModel(e, false);
                        box.close();
                    }
                });


            }
        }
    };

    private final Listener<TreeGridEvent<ModelData>> treeDoubleClickListener = new Listener<TreeGridEvent<ModelData>>() {
        @Override
        public void handleEvent(TreeGridEvent<ModelData> be) {
            ModelData selectedFolder = tree.getSelectionModel()
                    .getSelectedItem();

            if (selectedFolder != null) {
                GxtModel model = ((GxtModel) selectedFolder);
                Entity entity =  model.getBaseEntity();
                addEntityChildren(entity, model);
                notifyEntityClickedListener(entity);

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

    private final Listener<MessageBoxEvent> deleteEntityListener = new Listener<MessageBoxEvent>() {
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            final EntityServiceAsync service = GWT.create(EntityService.class);

            if (btn.getText().equals(Const.WORD_YES)) {
                final Entity entityToDelete = currentModel.getBaseEntity();
                service.deleteEntity(entityToDelete, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Error Deleting Point", caught);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        removeEntity(entityToDelete, currentModel);
                    }
                });

            }
        }
    };

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

    private final Listener<BaseEvent> addFileListener = new Listener<BaseEvent>() {
        @Override
        public void handleEvent(final BaseEvent be) {
            final Window w = new Window();
            w.setAutoWidth(true);
            w.setHeading(Const.MESSAGE_UPLOAD_SVG);
            FileUploadPanel p = new FileUploadPanel(UploadType.newFile);
            p.addFileAddedListeners(new FileUploadPanel.FileAddedListener() {

                @Override
                public void onFileAdded()  {
                    w.hide();
                    getUserEntities(true);
                }
            });

            w.add(p);
            w.show();
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
                        addUpdateTreeModel(e, true);
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