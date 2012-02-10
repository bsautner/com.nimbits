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
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.dnd.DND.*;
import com.extjs.gxt.ui.client.dnd.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.menu.*;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.controls.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.category.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.recordedvalues.*;
import com.nimbits.client.windows.*;
import com.nimbits.shared.*;

import java.util.*;


class NavigationPanel extends NavigationEventProvider {

    private final ContentPanel mainPanel;
    private Map<String, Entity> entityMap;
    private EntityTree<ModelData> tree;

    private TreeStore<ModelData> store;
    private Timer updater;
    private boolean expanded = false;
    private ClientType clientType;
    private Map<String, String> settings;
    private GxtModel currentModel;


    public NavigationPanel(final ClientType clientType,
                           final Map<String, String> settings) {

        this.settings = settings;
        this.clientType = clientType;

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
        getUserEntities();


    }

    private void addEntity(final Entity entity) {

        if (! entityMap.containsKey(entity.getUUID())) {
            entityMap.put(entity.getUUID(), entity);
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

        // doLayout(true);


    }

    private void updateModel(Value value, GxtModel model) {
        model.set(Const.PARAM_VALUE, value.getNumberValue());
        model.set(Const.PARAM_DATA, value.getData());
        model.set(Const.PARAM_TIMESTAMP, value.getTimestamp());
        model.set(Const.PARAM_NOTE, value.getNote());
        model.setAlertType(value.getAlertState());
        model.setDirty(false);
        store.update(model);
        notifyValueEnteredListener(entityMap.get(model.getUUID()), value);
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

//                if (selectedModel != null && selectedModel == tree.getTreeStore().getRootItems().get(0)) {
//                    e.setCancelled(true);
//                    e.getStatus().setStatus(false);
//                } else if (selectedModel != null) {
//                    if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_CATEGORY)) {
//                        e.setCancelled(true);
//                        e.getStatus().setStatus(false);
//                    }
//                }
            }

            @Override
            public void dragDrop(final DNDEvent e) {
                super.dragDrop(e);
                if (!(e.getTarget().getInnerHTML().equals("&nbsp;"))) {
                    if (selectedModel instanceof GxtModel) {
                        final GxtModel model = (GxtModel) selectedModel;
                        selectedModel.set(Const.PARAM_NAME, model.getName().getValue());
                        final Entity draggedEntity = entityMap.get(model.getUUID());
                        final Entity target = getDropTarget(e.getTarget().getInnerText());
                        if (target != null){
                            moveEntity(draggedEntity, target);
                        }


                    }

                }
            }
        });
    }

    private Entity getDropTarget(String targetName) {
        for (Entity entity : entityMap.values()) {
            if (entity.getName().getValue().equals(targetName)) {
                return entity;
            }
        }
        return null;
    }

    private void moveEntity(Entity draggedEntity, Entity target) {
        EntityServiceAsync service = GWT.create(EntityService.class);
        draggedEntity.setParentUUID(target.getUUID());
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

    private void reloadTree() throws NimbitsException {
        getUserEntities();
    }

    private void createPointPropertyWindow(Entity entity) {
        final Window window = new Window();
        final PointPanel panel = new PointPanel(entity);

        panel.addPointUpdatedListeners(new PointPanel.PointUpdatedListener() {
            @Override
            public void onPointUpdated(Entity result) {
                entityMap.remove(result.getUUID());
                entityMap.put(result.getUUID(), result);
                // points.remove(p.getName());
                // points.put(p.getName(), p) ;

                //	mp.setTopComponent(mainToolBar( points));
            }
        });

        panel.addPointDeletedListeners(new PointPanel.PointDeletedListener() {
            @Override
            public void onPointDeleted(Point p) {
                entityMap.remove(p.getUUID());


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


        for (Entity entity : result) {
            if (entity.getParentUUID().equals(model.getUUID())) {
                GxtModel model2 = new GxtModel(entity);
                if (parents.contains(entity.getUUID())) {
                    addChildrenToModel(result, parents, model2);
                }

                model.add(model2);
            }
        }
        // return model;
    }


    private void treeStoreBuilder(final List<Entity> result) {
        if (entityMap == null) {
            entityMap = new HashMap<String, Entity>();
        }
        entityMap.clear();
        List<ModelData> m = new ArrayList<ModelData>();
        List<String> parents = new ArrayList<String>();
        for (Entity entity : result) {
            if (! Utils.isEmptyString(entity.getParentUUID()) && ! parents.contains(entity.getParentUUID())) {
                parents.add(entity.getParentUUID());
            }
        }

        for (Entity entity : result) {

            addEntity(entity);
            if (entity.getEntityType().equals(EntityType.user)) {
                GxtModel userModel = new GxtModel(entity);
                addChildrenToModel(result, parents, userModel);

                m.add(userModel);
            }
        }

        store.add(m, true);

    }

    public void addNewlyCreatedEntityToTree(final Entity result) {


        if (! entityMap.containsKey(result.getUUID())) {
            entityMap.put(result.getUUID(), result);
            if (tree != null && tree.getStore() != null) {
                GxtModel model = new GxtModel(result);
                store = tree.getTreeStore();
                for (ModelData mx : store.getAllItems()) {
                    GxtModel m = (GxtModel)mx;
                    if (m.getUUID().equals(result.getParentUUID())) {
                        //  ((GxtModel) mx).add(model);
                        store.add(mx, model, true);
                        break;
                    }

                }
                //doLayout(true);
                // store.add(model, true);
                tree.setExpanded(model, true);

            } else {
                getUserEntities();

            }
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
            final CategoryServiceAsync service = GWT.create(CategoryService.class);
            service.getCategories(true, false, true,true, new AsyncCallback<List<Category>>() {
                @Override
                public void onFailure(final Throwable e) {
                    updater.cancel();
                    Info.display(Const.WORD_ERROR,
                            e.getMessage());
                }

                @Override
                public void onSuccess(final List<Category> categories) {
                    final TreeStore<ModelData> models = tree.getTreeStore();
                    final HashMap<String, Entity> map = new HashMap<String, Entity>();


                    for (final ModelData m : models.getAllItems()) {

                        final GxtModel model = (GxtModel) m;
                        if (!model.isDirty() && model.getEntityType().equals(EntityType.point)) {

                            if (map.containsKey(model.getUUID())) {
                                Entity e = map.get(model.getUUID());
                                model.setAlertType(e.getAlertType());
                                model.setValue(e.getValue());
                            }
                            models.update(m);
                        }

                    }
                }
            });
        }
    }

//    public void addPoint(final Entity p) {
//        store = tree.getTreeStore();
//
//
//        GxtModel pModel = new GxtModel(p);
//        store.remove(model);
//        model.add(pModel);
//        store.add(model, true);
//        if (!pointMap.containsKey(p.getUUID())) {
//            pointMap.put(p.getUUID(), p);
//        }
//        if (!categoryMap.containsKey(c.getName())) {
//            categoryMap.put(c.getName(), c);
//        }
//        tree.setExpanded(model, true);
//    }


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
            toolBar.add(addNewDiagramButton());
            toolBar.add(addNewCategoryButton());
        }
        toolBar.add(expandAllButton());

        return toolBar;
    }

    private Button addNewDiagramButton() {
        Button newDiagram = new Button();
        newDiagram.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.diagram()));
        newDiagram.setToolTip("Upload an SVG process diagram");

        newDiagram.addListener(Events.OnClick, addDiagramListener);
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
                                        addNewlyCreatedEntityToTree(c);
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
                MessageBox.confirm("Confirm", "Are you sure you want delete this? Doing so will permanently delete it including all of it's children (points, documents data etc)"
                        , deleteEntityListener);

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
                Entity entity = entityMap.get(selectedModel.getUUID());
                switch (selectedModel.getEntityType()) {
                    case category:  {


                        CategoryPropertyPanel dp = new CategoryPropertyPanel(entity);
                        dp.addEntityDeletedListeners(new EntityDeletedListener() {
                            @Override
                            public void onEntityDeleted(Entity entity1 )  {
                                entityMap.remove(entity1.getUUID());
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
                        WindowHelper.showSubscriptionPanel(entity, settings);
                        break;
                    }
                    case diagram: {
                        DiagramPropertyPanel dp = new DiagramPropertyPanel(entity);
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

    private Menu createContextMenu() {
        Menu contextMenu = new Menu();
        contextMenu.add(publishContext());
        contextMenu.add(currentStatusContext());
        contextMenu.add(propertyContext());
        contextMenu.add(copyContext());
        contextMenu.add(deleteContext());
        return contextMenu;
    }

    private MenuItem copyContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Copy");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_POINT)) {
                    GxtModel model = (GxtModel) selectedModel;
                    if (model.getEntityType().equals(EntityType.point)) {
                        //entityToBeCopied = entityMap.get(model.getUUID());
                        final MessageBox box = MessageBox.prompt(
                                Const.MESSAGE_NEW_POINT,
                                Const.MESSAGE_NEW_POINT_PROMPT);
                        box.addCallback(copyPointListener);
                    }
                }
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

                Entity p = entityMap.get(model.getUUID());
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

    private MenuItem currentStatusContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Current Status");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.form()));
        retObj.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                GxtModel model = (GxtModel) selectedModel;
                if (model.getEntityType().equals(EntityType.point) || model.getEntityType().equals(EntityType.category)) {
                    Entity p = entityMap.get(model.getUUID());
                    openUrl(p.getUUID(), p.getName().getValue());
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
                PointServiceAsync pointService = GWT.create(PointService.class);


                EntityName pointName = CommonFactoryLocator.getInstance().createName(newEntityName);
                //TODO
//                pointService.copyPoint(entityToBeCopied, pointName,
//                        new AsyncCallback<Point>() {
//                            @Override
//                            public void onFailure(Throwable caught) {
//                                Info.display("Could not create "
//                                        + newEntityName,
//                                        caught.getMessage());
//                                box.close();
//                            }
//
//                            @Override
//                            public void onSuccess(Point result) {
//                                try {
//                                    reloadTree();
//                                } catch (NimbitsException e) {
//                                    GWT.log(e.getMessage(), e);
//                                }
//                                box.close();
//                            }
//                        });
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
                EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName);
                Entity entity = EntityModelFactory.createEntity(name, EntityType.point);

                service.addUpdateEntity(entity, new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Info.display("Could not create "
                                + newEntityName,
                                caught.getMessage());
                        box.close();
                    }

                    @Override
                    public void onSuccess(Entity result) {
                        addNewlyCreatedEntityToTree(result);
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

                final Entity entity = entityMap.get(((GxtModel) selectedFolder).getId());
                notifyEntityClickedListener(entity);

            }
        }

    };

    private final Listener<MessageBoxEvent> deleteEntityListener = new Listener<MessageBoxEvent>() {
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            final EntityServiceAsync service = GWT.create(EntityService.class);

            if (btn.getText().equals(Const.WORD_YES)) {
                final Entity entityToDelete = entityMap.get(currentModel.getUUID());
                service.deleteEntity(entityToDelete, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //auto generated
                    }

                    @Override
                    public void onSuccess(Void result) {
                        //auto generated
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


                final Entity point = entityMap.get(model.getId());
                final Date timestamp = new Date();
                final Double v = model.get(Const.PARAM_VALUE);

                final Value value = ValueModelFactory.createValueModel(v, timestamp);
                RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
                service.recordValue(point, value, new AsyncCallback<Value>() {
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

    private final Listener<BaseEvent> addDiagramListener = new Listener<BaseEvent>() {
        @Override
        public void handleEvent(final BaseEvent be) {
            final Window w = new Window();
            w.setAutoWidth(true);
            w.setHeading(Const.MESSAGE_UPLOAD_SVG);
            DiagramUploadPanel p = new DiagramUploadPanel(UploadType.newFile);
            p.addDiagramAddedListeners(new DiagramUploadPanel.DiagramAddedListener() {
                @Override
                public void onDiagramAdded() throws NimbitsException {
                    w.hide();
                    reloadTree();
                }
            });

            w.add(p);
            w.show();
        }
    };

    //service calls
    private void getUserEntities()  {

        if (entityMap!=null) {
            entityMap.clear();
        }
        final EntityServiceAsync service = GWT.create(EntityService.class);
        service.getEntities(new AsyncCallback<List<Entity>>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(List<Entity> result) {
                createTree(result);
                doLayout();
            }
        });

    }


}