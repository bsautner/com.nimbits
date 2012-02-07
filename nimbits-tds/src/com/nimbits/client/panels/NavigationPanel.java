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
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.controls.EntityTree;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.UploadType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtDiagramModel;
import com.nimbits.client.model.GxtPointCategoryModel;
import com.nimbits.client.model.GxtPointModel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.client.service.category.CategoryService;
import com.nimbits.client.service.category.CategoryServiceAsync;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.datapoints.PointServiceAsync;
import com.nimbits.client.service.diagram.DiagramService;
import com.nimbits.client.service.diagram.DiagramServiceAsync;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;
import com.nimbits.client.windows.WindowHelper;
import com.nimbits.shared.Utils;

import java.util.*;


class NavigationPanel extends NavigationEventProvider {

    private final ContentPanel mainPanel;
    private GxtDiagramModel diagramModelToDelete;
    private GxtPointCategoryModel categoryModelToDelete;
    private GxtPointModel pointModelToDelete;
    private Point pointToBeCopied;

    private final Map<String, Point> pointMap = new HashMap<String, Point>();
    private final Map<EntityName, Category> categoryMap = new HashMap<EntityName, Category>();
    private final Map<EntityName, Diagram> diagramMap = new HashMap<EntityName, Diagram>();

    private EntityTree<ModelData> tree;
    private TreeStore<ModelData> store;
    private final boolean isConnectionPanel;
    private final EmailAddress email;
    private Timer updater;

    private ClientType clientType;
    private Map<String, String> settings;

    public NavigationPanel(final EmailAddress anEmailAddress,
                           final boolean isConnection,
                           final ClientType clientType,
                           final Map<String, String> settings) {

        this.settings = settings;

        mainPanel = new ContentPanel();
        mainPanel.setHeaderVisible(false);
        mainPanel.setFrame(false);
        mainPanel.setBodyBorder(false);
        mainPanel.setScrollMode(Scroll.AUTOY);
        mainPanel.setLayout(new FillLayout());
        setBorders(false);
        setScrollMode(Scroll.NONE);
        this.clientType = clientType;
        this.isConnectionPanel = isConnection;
        this.email = anEmailAddress;
        add(mainPanel);
        try {
            if (isConnectionPanel) {
                getConnectionEntities();
            } else {
                mainPanel.setTopComponent(treeToolBar());
                getUserEntities();
            }
        } catch (NimbitsException e) {
            GWT.log(e.getMessage(), e);
        }

    }

    private void createTree(final List<Category> result) {
        store = new TreeStore<ModelData>();
        ColumnConfigs columnConfigs = new ColumnConfigs();
        ColumnModel cm = new ColumnModel(
                Arrays.asList(
                        columnConfigs.pointNameColumn(true),
                        columnConfigs.currentValueColumn())
        );
        tree = new EntityTree<ModelData>(store, cm);
        tree.addListener(Events.AfterEdit, afterEditListener);



        treePropertyBuilder();
        treeStoreBuilder(result);
        treeDNDBuilder();

        final TreeGridDropTarget target = new TreeGridDropTarget(tree);
        target.setAllowSelfAsSource(!isConnectionPanel);
        target.setFeedback(Feedback.BOTH);
        mainPanel.removeAll();
        if (this.email != null && isConnectionPanel) {
            mainPanel.addText(email.getValue());
        }
        mainPanel.add(tree);
        doLayout(true);


    }

    private void updateModel(Value value, GxtPointModel model) {
        model.set(Const.PARAM_VALUE, value.getNumberValue());
        model.set(Const.PARAM_DATA, value.getData());
        model.set(Const.PARAM_TIMESTAMP, value.getTimestamp());
        model.set(Const.PARAM_NOTE, value.getNote());

        model.setAlertState(value.getAlertState());

//be.getRecord().commit(false);
        model.setDirty(false);
        store.update(model);
        notifyValueEnteredListener(pointMap.get(model.getUUID()), value);
    }

    private void treePropertyBuilder() {
        if (!isConnectionPanel) {
            tree.setContextMenu(createContextMenu());
        }

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

                if (selectedModel != null && selectedModel == tree.getTreeStore().getRootItems().get(0)) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                } else if (selectedModel != null) {
                    if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_CATEGORY)) {
                        e.setCancelled(true);
                        e.getStatus().setStatus(false);
                    }
                }
            }

            @Override
            public void dragDrop(final DNDEvent e) {
                super.dragDrop(e);

                if (!(e.getTarget().getInnerHTML().equals("&nbsp;")) && !isConnectionPanel) {
                    if (selectedModel instanceof GxtPointModel) {
                        final GxtPointModel model = (GxtPointModel) selectedModel;
                        selectedModel.set(Const.PARAM_NAME, model.getName().getValue());
                        final Point point = pointMap.get(model.getUUID());
                        PointServiceAsync pointService = GWT.create(PointService.class);

                        EntityName categoryName = CommonFactoryLocator.getInstance().createName(e.getTarget()
                                .getInnerText());
                        pointService.movePoint(point, categoryName, new AsyncCallback<Point>() {
                            @Override
                            public void onFailure(Throwable e) {
                                updater.cancel();
                                Info.display(Const.WORD_ERROR,
                                        e.getMessage());
                            }

                            @Override
                            public void onSuccess(Point result) {
                                // System.out.println();
                                Info.display("Point moved ", point.getName().getValue());
                            }
                        });

                    } else if (selectedModel instanceof GxtDiagramModel) {
                        final GxtDiagramModel gxtDiagramModel = (GxtDiagramModel) selectedModel;

                        selectedModel.set(Const.PARAM_NAME, gxtDiagramModel.getName());

                        DiagramServiceAsync diagramService = GWT.create(DiagramService.class);
                        EntityName categoryName = CommonFactoryLocator.getInstance().createName(e.getTarget().getInnerText());
                        diagramService.moveDiagram(gxtDiagramModel.getName(), categoryName, new AsyncCallback<Void>() {

                            @Override
                            public void onFailure(Throwable e) {
                                updater.cancel();
                                Info.display(Const.WORD_ERROR,
                                        e.getMessage());
                            }

                            @Override
                            public void onSuccess(Void aVoid) {
                                try {
                                    getUserEntities();
                                } catch (NimbitsException e1) {
                                    GWT.log(e1.getMessage(), e1);
                                }
                            }
                        });
                    }
                } else {
                    // final GxtPointModel gxtPointModel = (GxtPointModel) selectedModel;
                    // store.add(gxtPointModel, true);

                    try {
                        if (!isConnectionPanel) {
                            getUserEntities();
                        } else {
                            getConnectionEntities();
                        }
                    } catch (NimbitsException ignored) {
                    }
                }
            }
        });
    }

    private void reloadTree() throws NimbitsException {
        if (isConnectionPanel) {
            try {
                getConnectionEntities();
            } catch (NimbitsException ignored) {
            }
        } else {
            getUserEntities();
        }
    }

    private void createPointPropertyWindow(Point p) {
        final Window window = new Window();
        final PointPanel pp = new PointPanel(p);

        pp.addPointUpdatedListeners(new PointPanel.PointUpdatedListener() {
            @Override
            public void onPointUpdated(Point p) {
                pointMap.remove(p.getUUID());
                pointMap.put(p.getUUID(), p);
                // points.remove(p.getName());
                // points.put(p.getName(), p) ;

                //	mp.setTopComponent(mainToolBar( points));
            }
        });

        pp.addPointDeletedListeners(new PointPanel.PointDeletedListener() {
            @Override
            public void onPointDeleted(Point p) {
                pointMap.remove(p.getUUID());


            }
        });

        window.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connect()));
        window.setSize(466, 520);
        window.setPlain(false);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setHeading(p.getName().getValue() + " Properties");
        window.setHeaderVisible(true);
        window.setBodyBorder(true);
//	window.setLayout(new FitLayout());
        window.add(pp);
        window.show();
    }

    private void treeStoreBuilder(final List<Category> result) {
        pointMap.clear();
        diagramMap.clear();
        categoryMap.clear();


        for (final Category c : result) {
            final GxtPointCategoryModel gxtPointCategoryModel = new GxtPointCategoryModel(c, clientType);//  PointCategoryModelFactory.createPointCategoryModel(c);

            if (!categoryMap.containsKey(c.getName())) {
                categoryMap.put(c.getName(), c);
            }
            if (!c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY)) {
                if (!(c.getPoints() == null)) {
                    for (Point p : c.getPoints()) {
                        if (!pointMap.containsKey(p.getUUID())) {
                            pointMap.put(p.getUUID(), p);
                        }
                        p.setCatID(c.getId());
                        gxtPointCategoryModel.add(new GxtPointModel(p, clientType));
                    }
                }
                if (!(c.getDiagrams() == null)) {
                    for (Diagram d : c.getDiagrams()) {
                        if (!diagramMap.containsKey(d.getName())) {
                            diagramMap.put(d.getName(), d);
                        }
                        d.setCategoryFk(c.getId());
                        gxtPointCategoryModel.add(new GxtDiagramModel(d));
                    }
                }
                store.add(gxtPointCategoryModel, true);
            }
        }

        for (Category c : result) {
            if (c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY) && (c.getPoints() != null)) {
                for (Point p : c.getPoints()) {
                    if (!pointMap.containsKey(p.getUUID())) {
                        pointMap.put(p.getUUID(), p);
                        p.setCatID(c.getId());

                        store.add(new GxtPointModel(p, clientType), false);
                    }

                }
                if (c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY) && (c.getDiagrams() != null)) {
                    for (Diagram d : c.getDiagrams()) {
                        if (!diagramMap.containsKey(d.getName())) {
                            diagramMap.put(d.getName(), d);
                        }
                        d.setCategoryFk(c.getId());
                        store.add(new GxtDiagramModel(d), false);
                    }

                }
            }
        }
    }

    public void addNewlyCreatedPointToTree(final Point result) {
        if (! pointMap.containsKey(result.getUUID())) {
            pointMap.put(result.getUUID(), result);
            if (tree != null && tree.getStore() != null) {
                GxtPointModel model = new GxtPointModel(result, clientType);
                store = tree.getTreeStore();
                store.add(model, true);
                tree.setExpanded(model, true);

            } else {
                try {
                    getUserEntities();
                } catch (NimbitsException e) {
                    Info.display(Const.WORD_ERROR, e.getMessage());
                }
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
        // updater.schedule(1000);
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
                    final HashMap<EntityName, Point> pointHashMap = new HashMap<EntityName, Point>();

                    for (final Category c : categories) {
                        if (c.getPoints() != null) {
                            for (final Point p : c.getPoints()) {
                                //  if (p.isHighAlarmOn() || p.isLowAlarmOn() || p.isIdleAlarmOn()) {
                                pointHashMap.put(p.getName(), p);
                                //  }
                            }
                        }
                    }

                    for (final ModelData m : models.getAllItems()) {
                        if (m instanceof GxtPointModel) {
                            final GxtPointModel gxtPointModel = (GxtPointModel) m;
                            if (!((GxtPointModel) m).isDirty()) {
                                if (pointHashMap.containsKey(((GxtPointModel) m).getName())) {
                                    gxtPointModel.setAlertState(pointHashMap.get(gxtPointModel.getName()).getAlertState());
                                    gxtPointModel.setValue(pointHashMap.get(gxtPointModel.getName()).getValue());
                                }
                                models.update(m);
                            }
                        }
                    }
                }
            });
        }
    }

    public void addPoint(final Point p, final Category c) {
        store = tree.getTreeStore();
        c.getPoints().add(p);
        GxtPointCategoryModel model = new GxtPointCategoryModel(c, clientType);
        GxtPointModel pModel = new GxtPointModel(p, clientType);
        store.remove(model);
        model.add(pModel);
        store.add(model, true);
        if (!pointMap.containsKey(p.getUUID())) {
            pointMap.put(p.getUUID(), p);
        }
        if (!categoryMap.containsKey(c.getName())) {
            categoryMap.put(c.getName(), c);
        }
        tree.setExpanded(model, true);
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
    private boolean expanded = false;
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

                        final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);

                        categoryService.addCategory(categoryName,
                                new AsyncCallback<Category>() {
                                    @Override
                                    public void onFailure(Throwable caught) {
                                        updater.cancel();
                                        Info.display(Const.WORD_ERROR,
                                                caught.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(final Category c) {
                                        final GxtPointCategoryModel m = new GxtPointCategoryModel(c, clientType);// PointCategoryModelFactory.createPointCategoryModel(c);
                                        categoryMap.put(c.getName(), c);

                                        if (store != null) {
                                            store = tree.getTreeStore();
                                            store.add(m, true);

                                            tree.setExpanded(m, true);
                                            final String v = Format.ellipse(
                                                    newEntityName, 80);
                                            Info.display(Const.WORD_SUCCESS,
                                                    "New Category added: '{0}'",
                                                    new Params(v));
                                            layout(true);
                                            try {
                                                if (!isConnectionPanel) {
                                                    getUserEntities();
                                                } else {
                                                    getConnectionEntities();
                                                }
                                            } catch (NimbitsException ignored) {
                                            }
                                        } else {
                                            try {
                                                getUserEntities();
                                            } catch (NimbitsException e) {
                                                Info.display(Const.WORD_ERROR, e.getMessage());
                                            }
                                        }
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
                if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_CATEGORY)) {
                    categoryModelToDelete = (GxtPointCategoryModel) selectedModel;

                    MessageBox.confirm("Confirm", "Are you sure you want delete this category? Doing so will permanently delete all of the points in this category along with their date"
                            , deleteCategoryListener);
                } else if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_POINT)) {
                    pointModelToDelete = (GxtPointModel) selectedModel;
                    if (pointModelToDelete.getEntityType().equals(EntityType.point)) {
                        MessageBox.confirm("Confirm", "Are you sure you want delete this Point? Doing so will permanently delete all of its historical data"
                                , deletePointListener);
                    }
                    else if (((GxtPointModel) selectedModel).getEntityType().equals(EntityType.subscription)) {
                        MessageBox.confirm("Confirm", "Are you sure you want delete this subscription?"
                                , deletePointListener);
                    }
                } else if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_DIAGRAM)) {
                    diagramModelToDelete = (GxtDiagramModel) selectedModel;

                    MessageBox.confirm("Confirm", "Are you sure you want delete this Diagram?"
                            , deleteDiagramListener);
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
                ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_CATEGORY)) {

                    GxtPointCategoryModel model = (GxtPointCategoryModel) selectedModel;
                    Category c = categoryMap.get(model.getName());
                    CategoryPropertyPanel dp = new CategoryPropertyPanel(c, c.isReadOnly());
                    dp.addCategoryDeletedListeners(new CategoryDeletedListener() {
                        @Override
                        public void onCategoryDeleted(Category c, boolean readOnly)  {
                            categoryMap.remove(c.getName());
                        }
                    });

                    final Window w = new Window();
                    w.setWidth(500);
                    w.setHeight(400);
                    w.setHeading(c.getName() + " " + Const.WORD_PROPERTIES);
                    w.add(dp);
                    w.show();
                } else if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_POINT)) {

                    GxtPointModel model = ((GxtPointModel) selectedModel);
                    Point p = pointMap.get(model.getUUID());
                    if (p.getEntityType().equals(EntityType.point)) {
                        createPointPropertyWindow(p);
                    }
                    else if (p.getEntityType().equals(EntityType.subscription)) {
                        WindowHelper.showSubscriptionPanel(p.getUUID(), settings);
                    }


                } else if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_DIAGRAM)) {
                    GxtDiagramModel diagramModel = (GxtDiagramModel) selectedModel;
                    Diagram diagram = diagramMap.get(diagramModel.getName());

                    DiagramPropertyPanel dp = new DiagramPropertyPanel(diagram, diagram.isReadOnly());
                    final Window w = new Window();
                    w.setWidth(500);
                    w.setHeight(400);
                    w.setHeading(diagram.getName() + " " + Const.WORD_PROPERTIES);
                    w.add(dp);
                    w.show();
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
                    GxtPointModel model = (GxtPointModel) selectedModel;
                    if (model.getEntityType().equals(EntityType.point)) {
                        pointToBeCopied = pointMap.get(model.getUUID());
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

                if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_POINT)) {
                    GxtPointModel model = (GxtPointModel) selectedModel;
                    if (model.getEntityType().equals(EntityType.point)) {
                        publishPoint(model);
                    }

                } else if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_CATEGORY)) {
                    publishCategory((GxtPointCategoryModel) selectedModel);
                }
            }

            private void publishPoint(GxtPointModel model) {

                Point p = pointMap.get(model.getUUID());
                PointServiceAsync pointService = GWT.create(PointService.class);

                pointService.publishPoint(p, new AsyncCallback<Point>() {

                    @Override
                    public void onFailure(Throwable e) {
                        updater.cancel();
                        Info.display(Const.WORD_ERROR,
                                e.getMessage());
                    }

                    @Override
                    public void onSuccess(Point point) {
                        com.google.gwt.user.client.Window.alert("Your data points is now set to public, people can now discover it by" +
                                " searching for its name or description on nimbits.com. Please select the property option to edit these values.");

                    }
                });

            }

            private void publishCategory(GxtPointCategoryModel model) {

                Category p = categoryMap.get(model.getName());
                CategoryServiceAsync service = GWT.create(CategoryService.class);

                service.publishCategory(p, new AsyncCallback<Category>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        updater.cancel();
                        Info.display(Const.WORD_ERROR,
                                throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(Category category) {
                        com.google.gwt.user.client.Window.alert("Your category, and all of its data points are now set to public, people can now discover it by" +
                                " searching for its name or description on nimbits.com. Please select the property option to edit these values.");
                    }
                });

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

                if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_POINT)) {
                    GxtPointModel model = (GxtPointModel) selectedModel;
                    Point p = pointMap.get(model.getUUID());
                    openUrl(p.getUUID(), p.getName().getValue());

                }
                if (selectedModel.get(Const.PARAM_ICON).equals(Const.PARAM_CATEGORY)) {
                    GxtPointCategoryModel model = (GxtPointCategoryModel) selectedModel;
                    Category c = categoryMap.get(model.getName());
                    openUrl(c.getUUID(), c.getName().getValue());
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

                pointService.copyPoint(pointToBeCopied, pointName,
                        new AsyncCallback<Point>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                Info.display("Could not create "
                                        + newEntityName,
                                        caught.getMessage());
                                box.close();
                            }

                            @Override
                            public void onSuccess(Point result) {
                                try {
                                    reloadTree();
                                } catch (NimbitsException e) {
                                    GWT.log(e.getMessage(), e);
                                }
                                box.close();
                            }
                        });
            }
        }
    };

    private final Listener<MessageBoxEvent> deleteCategoryListener = new Listener<MessageBoxEvent>() {
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);

            if (btn.getText().equals(Const.WORD_YES)) {
                Category categoryToDelete = categoryMap.get(categoryModelToDelete.getName());

                categoryService.deleteCategory(categoryToDelete, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        for (ModelData modelData : categoryModelToDelete.getChildren()) {
                            GxtPointModel pointModel = (GxtPointModel) modelData;
                            Point point = pointMap.get(pointModel.getUUID());
                            notifyPointDeletedListener(point);

                        }

                        tree.getStore().remove(categoryModelToDelete);
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
                PointServiceAsync pointService = GWT.create(PointService.class);


                EntityName pointName = CommonFactoryLocator.getInstance().createName(newEntityName);


                pointService.addPoint(pointName, new AsyncCallback<Point>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Info.display("Could not create "
                                + newEntityName,
                                caught.getMessage());
                        box.close();
                    }

                    @Override
                    public void onSuccess(Point result) {
                        addNewlyCreatedPointToTree(result);
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
                // String icon = selectedFolder.getInstance("icon");

                if (selectedFolder instanceof GxtPointCategoryModel) {
                    final Category category = categoryMap.get(((GxtPointCategoryModel) selectedFolder).getName());

                    notifyCategoryClickedListener(category, isConnectionPanel);

                } else if (selectedFolder instanceof GxtPointModel) {
                    Point point = pointMap.get(((GxtPointModel) selectedFolder).getUUID());
                    point.setReadOnly(isConnectionPanel);
                    point.setClientType(ClientType.other);
                    notifyPointClickedListener(point);

                } else if (selectedFolder instanceof GxtDiagramModel) {
                    Diagram diagram = diagramMap.get(((GxtDiagramModel) selectedFolder).getName());
                    diagram.setClientType(ClientType.other);
                    diagram.setReadOnly(isConnectionPanel);
                    notifyDiagramClickedListener(diagram);
                }
            } else {
                try {
                    getUserEntities();
                } catch (NimbitsException ignored) {
                }
            }
        }
    };

    private final Listener<MessageBoxEvent> deletePointListener = new Listener<MessageBoxEvent>() {
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            final PointServiceAsync service = GWT.create(PointService.class);

            if (btn.getText().equals(Const.WORD_YES)) {
                final Point pointToDelete = pointMap.get(pointModelToDelete.getUUID());

                service.deletePoint(pointToDelete, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                    }

                    @Override
                    public void onSuccess(Void aVoid) {

                        notifyPointDeletedListener(pointToDelete);
                        GWT.log("Deleted " + pointToDelete.getName().getValue());


                        tree.getStore().remove(pointModelToDelete);
                    }
                });

            }
        }
    };

    private final Listener<MessageBoxEvent> deleteDiagramListener = new Listener<MessageBoxEvent>() {
        public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            final DiagramServiceAsync service = GWT.create(DiagramService.class);

            if (btn.getText().equals(Const.WORD_YES)) {
                final Diagram diagramToDelete = diagramMap.get(diagramModelToDelete.getName());
                service.deleteDiagram(diagramToDelete, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyDiagramDeletedListener(diagramToDelete, false);
                        GWT.log("Deleted " + diagramToDelete.getName());
                        tree.getStore().remove(diagramModelToDelete);
                    }
                });
            }
        }
    };

    private final Listener<GridEvent> afterEditListener = new Listener<GridEvent>() {

        @Override
        public void handleEvent(final GridEvent be) {
            final GxtPointModel model = (GxtPointModel) be.getModel();
            if (!model.isReadOnly()) {
                model.setDirty(true);


                final Point point = pointMap.get(model.getUUID());
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
    private void getUserEntities() throws NimbitsException {

        final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
        categoryService.getCategories(true, true, true,true,
                new AsyncCallback<List<Category>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log(caught.getMessage(), caught);
                    }

                    @Override
                    public void onSuccess(List<Category> result) {
                        createTree(result);
                        doLayout();
                    }
                });
    }

    private void getConnectionEntities() throws NimbitsException {


        final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
        categoryService.getConnectionCategories(true, true, true, email,
                new AsyncCallback<List<Category>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                        GWT.log(caught.getMessage(), caught);

                    }

                    @Override
                    public void onSuccess(List<Category> result) {
                        createTree(result);

                        doLayout();

                    }
                });
    }
}