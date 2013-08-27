/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.ui.controls;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.UserMessages;
import com.nimbits.cloudplatform.client.constants.Words;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.GxtModel;
import com.nimbits.cloudplatform.client.model.TreeModel;
import com.nimbits.cloudplatform.client.model.category.Category;
import com.nimbits.cloudplatform.client.model.category.CategoryFactory;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.service.entity.EntityService;
import com.nimbits.cloudplatform.client.service.entity.EntityServiceAsync;
import com.nimbits.cloudplatform.client.ui.controls.menu.AddFolderMenuItem;
import com.nimbits.cloudplatform.client.ui.controls.menu.AddPointMenuItem;
import com.nimbits.cloudplatform.client.ui.helper.FeedbackHelper;
import com.nimbits.cloudplatform.client.ui.icons.Icons;
import com.nimbits.cloudplatform.client.ui.panels.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/17/12
 * Time: 9:44 AM
 */
public class EntityContextMenu extends Menu {
    private static final String SCHEDULE_A_DATA_DUMP = "Schedule a data dump";
    private static final String VIEW_REPORT = "View Report";
    private static final String GET_JSON_STRUCTURE = "Get JSON Structure";
    private static final String SUBSCRIBE_TO_EVENTS = "Subscribe to Events";
    private static final String TEXT = "Edit Calculation";
    public static final String PLEASE_SELECT_A_PARENT = "Please select a parent. You may not have clicked on the item in the tree you're creating a new point under, if you're a new user, try clicking your email address first.";
    private final Listener<MessageBoxEvent> createNewPointListener = new NewPointMessageBoxEventListener();
    private final Listener<MessageBoxEvent> createNewFolderListener = new NewFolderMessageBoxEventListener();
    private final Listener<MessageBoxEvent> deleteEntityListener = new DeleteMessageBoxEventListener();
    private final Listener<MessageBoxEvent> copyPointListener  = new CopyPointMessageBoxEventListener();


    public static final String MESSAGE_NEW_POINT = "New Data Point";


    private class NewPointMessageBoxEventListener implements Listener<MessageBoxEvent> {

        private String newEntityName;

        NewPointMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {


                    final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                    currentModel = (TreeModel)selectedModel;
                    if (currentModel != null) {
                        final MessageBox box = MessageBox.wait("Progress",
                                "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                        box.show();
                        EntityServiceAsync service = GWT.create(EntityService.class);

                        final Entity currentEntity =  currentModel.getBaseEntity();
                        try {
                        EntityName name = CommonFactory.createName(newEntityName, EntityType.point);
                        Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                        Point p = PointModelFactory.createPointModel(entity, 0.0, 90, "", 0.0,
                                    false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false,
                                    PointType.basic, 0, false, 0.0);
                            p.setParent(currentEntity.getKey());
                            service.addUpdateEntityRpc(Arrays.<Entity>asList(p), new NewPointEntityAsyncCallback(box));
                        } catch (Exception e) {
                            box.close();
                            FeedbackHelper.showError(e);
                        }


                    }
                    else {
                        FeedbackHelper.showError(new Exception(PLEASE_SELECT_A_PARENT));
                    }



            }
        }


        private class NewPointEntityAsyncCallback implements AsyncCallback<List<Entity>> {
            private final MessageBox box;

            NewPointEntityAsyncCallback(MessageBox box) {
                this.box = box;
            }

            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
                box.close();
            }

            @Override
            public void onSuccess(List<Entity> result) {

                try {
                    notifyEntityModifiedListener(new GxtModel(result.get(0)), Action.create);
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

                box.close();
            }
        }
    }



    private class NewFolderMessageBoxEventListener implements Listener<MessageBoxEvent> {

        private String newEntityName;

        NewFolderMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {


                final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (TreeModel)selectedModel;
                if (currentModel != null) {
                    final MessageBox box = MessageBox.wait("Progress",
                            "Creating your folder", "Creating: " + newEntityName);
                    box.show();
                    EntityServiceAsync service = GWT.create(EntityService.class);

                    final Entity currentEntity =  currentModel.getBaseEntity();
                    try {
                        EntityName name = CommonFactory.createName(newEntityName, EntityType.category);
                        Entity entity = EntityModelFactory.createEntity(name, EntityType.category);
                        Category p = CategoryFactory.createCategory(entity);
                        p.setParent(currentEntity.getKey());
                        service.addUpdateEntityRpc(Arrays.<Entity>asList(p), new NewFolderEntityAsyncCallback(box));
                    } catch (Exception e) {
                        box.close();
                        FeedbackHelper.showError(e);
                    }


                }
                else {
                    FeedbackHelper.showError(new Exception("Please select a parent"));
                }



            }
        }


        private class NewFolderEntityAsyncCallback implements AsyncCallback<List<Entity>> {
            private final MessageBox box;

            NewFolderEntityAsyncCallback(MessageBox box) {
                this.box = box;
            }

            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
                box.close();
            }

            @Override
            public void onSuccess(List<Entity> result) {

                try {
                    notifyEntityModifiedListener(new GxtModel(result.get(0)), Action.create);
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

                box.close();
            }
        }
    }



    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private EntityTree<ModelData> tree;
    private TreeModel currentModel;
    private static final String PARAM_DEFAULT_WINDOW_OPTIONS = "menubar=no," +
            "Location=false," +
            "resizable=yes," +
            "scrollbars=yes," +
            "width=980px," +
            "height=800," +
            "status=no," +
            "dependent=true";

    private MenuItem deleteContext;
    private MenuItem subscribeContext;
    private MenuItem reportContext;
    private MenuItem copyContext;
    private MenuItem calcContext;
    private MenuItem summaryContext;
    private MenuItem keyContext;
    private MenuItem jsonContext;
    private MenuItem propertyContext;
    private MenuItem dumpContext;
    private MenuItem uploadContext;
    private AddPointMenuItem addPointMenuItem;
    private AddFolderMenuItem addFolderMenuItem;

    private Map<String, String> settings;
    private final User user;

    private List<EntityModifiedListener> entityModifiedListeners;

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    private void notifyEntityModifiedListener(final TreeModel model, final Action action)  {
        for (final EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }

    public interface EntityModifiedListener {
        void onEntityModified(final TreeModel model, final Action action) ;

    }

    private class NewPointBaseEventListener implements Listener<BaseEvent> {
        NewPointBaseEventListener() {
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            final MessageBox box = MessageBox.prompt(
                     MESSAGE_NEW_POINT,
                    UserMessages.MESSAGE_NEW_POINT_PROMPT);

            box.addCallback(createNewPointListener);
        }
    }

    private class NewFolderBaseEventListener implements Listener<BaseEvent> {
        NewFolderBaseEventListener() {
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            final MessageBox box = MessageBox.prompt(
                    "Add a new folder",
                    UserMessages.MESSAGE_ADD_CATEGORY);

            box.addCallback(createNewFolderListener);
        }
    }


    public EntityContextMenu(final User user, final EntityTree<ModelData> tree, final Map<String, String> settings) {
        super();

        propertyContext = propertyContext();
        addPointMenuItem = new AddPointMenuItem();
        addFolderMenuItem = new AddFolderMenuItem();
        addPointMenuItem.addListener(Events.OnClick, new NewPointBaseEventListener());
        addFolderMenuItem.addListener(Events.OnClick, new NewFolderBaseEventListener());

        this.user = user;
        entityModifiedListeners = new ArrayList<EntityModifiedListener>(1);
        this.tree = tree;

        this.settings = settings;
        deleteContext = deleteContext();
        subscribeContext = subscribeContext();
        reportContext = reportContext();
        copyContext = copyContext();
        calcContext = calcContext();

       // xmppContext = xmppResourceContext();
        summaryContext = summaryContext();

        keyContext = keyContext();
    //    exportContext = exportContext();
        dumpContext = dumpContext();
        uploadContext = uploadContext();
        jsonContext = jsonContext();
        add(addPointMenuItem);
        add(addFolderMenuItem);
        add(propertyContext);
      //  add(exportContext);
        add(dumpContext);
        add(uploadContext);
        add(copyContext);
        add(deleteContext);
        add(subscribeContext);
        add(reportContext);
        add(keyContext);
        add(calcContext);
        add(summaryContext);

        add(jsonContext);






    }



    @Override
    public void showAt(final int x, final int y) {
        super.showAt(x, y);
        final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
        currentModel = (TreeModel)selectedModel;
        deleteContext.setEnabled(!currentModel.getEntityType().equals(EntityType.user) || ! currentModel.isReadOnly()  );
        subscribeContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) ||currentModel.getEntityType().equals(EntityType.category));
        reportContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.category));
        copyContext.setEnabled(currentModel.getEntityType().equals(EntityType.point));
        calcContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.calculation));
        summaryContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.summary));

        jsonContext.setEnabled(! currentModel.getEntityType().equals(EntityType.user));
        keyContext.setEnabled(currentModel.getEntityType().equals(EntityType.user) || currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.accessKey));
        //exportContext.setEnabled(currentModel.getEntityType().equals(EntityType.point));// && isDomain);
        propertyContext.setEnabled(!currentModel.isReadOnly());
        // addPointMenuItem.setEnabled(!currentModel.isReadOnly() );
        //downloadContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) ||currentModel.getEntityType().equals(EntityType.category));


    }



    private MenuItem deleteContext() {
        final MenuItem retObj = new MenuItem();


        retObj.setText("Delete");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.delete()));
        retObj.addSelectionListener(new DeleteMenuEventSelectionListener());
        return retObj;


    }

    private MenuItem calcContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText("Calculation");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.formula()));
        retObj.addSelectionListener(new CalcMenuEventSelectionListener());
        return retObj;
    }



    private MenuItem summaryContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText("Summarize");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.expand()));
        retObj.addSelectionListener(new SummaryMenuEventSelectionListener());
        return retObj;

    }


    private MenuItem keyContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText("New Read/Write Key");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.key()));
        retObj.addSelectionListener(new KeyMenuEventSelectionListener());
        return retObj;

    }
//    private MenuItem exportContext() {
//        final MenuItem retObj = new MenuItem();
//
//        retObj.setText("Export to Google Drive&trade;");
//
//        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.download()));
//        retObj.addSelectionListener(new DownloadEventSelectionListener());
//        return retObj;
//
//    }

    private MenuItem dumpContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText(SCHEDULE_A_DATA_DUMP);

        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.download()));
        retObj.addSelectionListener(new DumpEventSelectionListener());
        return retObj;

    }

    private MenuItem uploadContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText("Upload Data");

        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.diagram()));
        retObj.addSelectionListener(new UploadEventSelectionListener());
        return retObj;

    }





    public void showSummaryPanel(final Entity entity) {
        SummaryPanel dp = new SummaryPanel(user, entity);
        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        w.setHeading("Summary data relay");
        w.add(dp);
        dp.addEntityAddedListener(new SummaryEntityAddedListener(w));

        w.show();
    }



    public void showKeyPanel(final Entity entity) {
        AccessKeyPanel dp = new AccessKeyPanel(entity);
        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        w.setHeading("Read/Write Key");
        w.add(dp);
        dp.addEntityAddedListener(new EntityAddedListener(w));

        w.show();
    }
//    public void showDownloadPanel(final Entity entity) {
//        DownloadPanel dp = new DownloadPanel(entity);
//        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
//        w.setWidth(WIDTH);
//        w.setHeight(350);
//        w.setHeading("Export To Google Drive&trade;");
//        w.add(dp);
//
//
//        w.show();
//    }
    public void showDumpPanel(final Entity entity) {
        DumpPanel dp = new DumpPanel(entity);
        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(350);
        w.setHeading(SCHEDULE_A_DATA_DUMP);
        w.add(dp);


        w.show();
    }
    public void showUploadPanel(final Entity entity) {
        DataUploadPanel dp = new DataUploadPanel(entity);
        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(350);
        w.setHeading("Upload Data");
        w.add(dp);


        w.show();
    }


    private MenuItem propertyContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText("Edit Properties");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.edit()));
        retObj.addSelectionListener(new EditMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem subscribeContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText("Subscribe To Events");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.plugin()));
        retObj.addSelectionListener(new SubscribeMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem copyContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText("Copy Entity");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));
        retObj.addSelectionListener(new CopyMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem reportContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(VIEW_REPORT);
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.form()));
        retObj.addSelectionListener(new ReportMenuEventSelectionListener());

        return retObj;
    }

    private MenuItem jsonContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(GET_JSON_STRUCTURE);
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.json()));
        retObj.addSelectionListener(new JsonMenuEventSelectionListener());

        return retObj;
    }



    public void showSubscriptionPanel(final Entity entity) {
        final SubscriptionPanel dp = new SubscriptionPanel(user, entity);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        w.setHeading(SUBSCRIBE_TO_EVENTS);
        w.add(dp);
        dp.addEntityAddedListener(new SubscribeEntityAddedListener(w));

        w.show();
    }

    public void showCalcPanel(final Entity entity)  {
        final CalculationPanel dp = new CalculationPanel(user, entity);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        if (entity.getEntityType().equals(EntityType.point)) {
            w.setHeading("Calculations triggered when data is recorded to " + entity.getName().getValue());
        }
        else {
            w.setHeading(TEXT);

        }
        w.add(dp);
        dp.addEntityAddedListener(new EntityAddedListener(w));

        w.show();
    }

    private class EntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        EntityAddedListener(final Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(final List<Entity> entity)  {
            w.hide();
            notifyEntityModifiedListener(new GxtModel(entity.get(0)), Action.create);

        }
    }

    private class DeleteMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        DeleteMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel)selectedModel;
            if (! currentModel.isReadOnly()) {
                MessageBox.confirm("Confirm", "Are you sure you want delete this? Doing so will permanently delete it including all of it's children (points, documents data etc)"
                        , deleteEntityListener);
            }

        }
    }

    private class CalcMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        CalcMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            final Entity entity = selectedModel.getBaseEntity();
            try {
                showCalcPanel(entity);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }

    }

    private class CopyEntityAsyncCallback implements AsyncCallback<List<Entity>> {
        private final MessageBox box;

        CopyEntityAsyncCallback(final MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(final Throwable caught) {
            box.close();
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final List<Entity> entity) {
            box.close();
            try {
                final TreeModel model = new GxtModel(entity.get(0));
                notifyEntityModifiedListener(model, Action.create);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }

            //  addUpdateTreeModel(entity, false);
        }
    }

    private class CopyPointMessageBoxEventListener implements Listener<MessageBoxEvent> {
        private String newEntityName;

        CopyPointMessageBoxEventListener() {
        }


        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                final EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = null;
                try {
                    name = CommonFactory.createName(newEntityName, EntityType.point);
                } catch (Exception caught) {
                    FeedbackHelper.showError(caught);
                }
                final Entity entity =  currentModel.getBaseEntity();

                service.copyEntity(entity, name, new CopyEntityAsyncCallback(box));

            }
        }
    }

    private class DeleteEntityAsyncCallback implements AsyncCallback<List<Entity>> {
        DeleteEntityAsyncCallback() {
        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final List<Entity> result) {
            try {
                notifyEntityModifiedListener(currentModel, Action.delete);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }

        }
    }

    private class ReportMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        ReportMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            final TreeModel model = (TreeModel) selectedModel;
            if (model.getEntityType().equals(EntityType.point) || model.getEntityType().equals(EntityType.category)) {
                final Entity p =  model.getBaseEntity();
                openUrl(p.getUUID(), p.getName().getValue());

            }



        }

        private void openUrl(final String uuid, final String title) {
            String u = com.google.gwt.user.client.Window.Location.getHref()
                    + "report.html?uuid=" + uuid
                    + "&count=10";
            u = u.replace("/#?", "?");
            com.google.gwt.user.client.Window.open(u, title, PARAM_DEFAULT_WINDOW_OPTIONS);
        }
    }
    private class JsonMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        JsonMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            final TreeModel model = (TreeModel) selectedModel;
            final Entity p =  model.getBaseEntity();
            openUrl(p.getKey(), p.getName().getValue());





        }

        private void openUrl(final String id, final String title) {
            String u = com.google.gwt.user.client.Window.Location.getHref()
                    + "service/entity?id=" + id;
            u = u.replace("/#?", "?");
            com.google.gwt.user.client.Window.open(u, title, PARAM_DEFAULT_WINDOW_OPTIONS);
        }
    }
    private class SubscribeMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SubscribeMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity =  currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.subscription)  ||
                    entity.getEntityType().equals(EntityType.point)) {
                showSubscriptionPanel(entity);
            }

        }
    }

    private class EditMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        EditMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            final Entity entity = selectedModel.getBaseEntity();

            try {
                switch (selectedModel.getEntityType()) {
                    case category:  {


                        final CategoryPropertyPanel dp = new CategoryPropertyPanel(entity);
                        final Window w = new Window();
                        w.setWidth(WIDTH);
                        w.setHeight(HEIGHT);
                        w.setHeading(entity.getName().getValue() + ' ' + Words.WORD_PROPERTIES);

                        w.add(dp);
                        w.show();
                        break;

                    }
                    case point:


                        createPointPropertyWindow(entity);


                        break;


                    case subscription:
                        showSubscriptionPanel(entity);
                        break;
                    case calculation:

                        showCalcPanel(entity);

                        break;

                    case summary:
                        showSummaryPanel(entity);
                        break;

                }
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }


        private void createPointPropertyWindow(final Entity entity)  {
            final Window window = new Window();


            final PointPanel panel = new PointPanel(entity);

            panel.addPointUpdatedListeners(new PointUpdatedListener());



            window.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connect()));
            window.setWidth(WIDTH);
            window.setHeight(HEIGHT);
            window.setPlain(false);
            window.setModal(true);
            window.setBlinkModal(true);
            window.setHeading(entity.getName().getValue() + " Properties");
            window.setHeaderVisible(true);
            window.setBodyBorder(true);

            window.add(panel);
            window.show();
        }

        private class PointUpdatedListener implements PointPanel.PointUpdatedListener {
            PointUpdatedListener() {
            }

            @Override
            public void onPointUpdated(final Entity result)  {
                notifyEntityModifiedListener(new GxtModel(result), Action.create);
            }
        }
    }

    private class SubscribeEntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        SubscribeEntityAddedListener(final Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(final List<Entity> entity)  {
            w.hide();
            Cookies.removeCookie(Action.subscribe.name());
            notifyEntityModifiedListener(new GxtModel(entity.get(0)), Action.create);

        }
    }




    private class DeleteMessageBoxEventListener implements Listener<MessageBoxEvent> {
        DeleteMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent ce) {
            final com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();
            final EntityServiceAsync service = GWT.create(EntityService.class);

            if (btn.getText().equals(Words.WORD_YES)) {
                final Entity entityToDelete = currentModel.getBaseEntity();
                service.deleteEntityRpc(Arrays.<Entity>asList(entityToDelete), new DeleteEntityAsyncCallback());

            }
        }
    }

    private class CopyMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        CopyMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final MessageBox box;
            if (currentModel.getEntityType().equals(EntityType.point) && ! currentModel.isReadOnly()) {
                box= MessageBox.prompt(
                        MESSAGE_NEW_POINT,
                        UserMessages.MESSAGE_NEW_POINT_PROMPT);
                box.addCallback(copyPointListener);
            }
            else {
                box = MessageBox.alert("Not supported", "Sorry, for the moment you can only copy your data points", null);
            }
            box.show();
        }
    }



    private class SummaryEntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        SummaryEntityAddedListener(final Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(final List<Entity> entity)  {
            w.hide();
            Cookies.removeCookie(Action.subscribe.name());
//                if (entity.getEntityType().equals(EntityType.point)) {
            notifyEntityModifiedListener(new GxtModel(entity.get(0)), Action.create);
//                }


        }
    }

    private class SummaryMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SummaryMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity =  currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.subscription)  ||
                    entity.getEntityType().equals(EntityType.point)) {
                showSummaryPanel(entity);
            }

        }
    }



    private class KeyMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        KeyMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity =  currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.accessKey)  ||
                    entity.getEntityType().equals(EntityType.point) || entity.getEntityType().equals(EntityType.user)) {
                showKeyPanel(entity);
            }

        }
    }
//    private class DownloadEventSelectionListener extends SelectionListener<MenuEvent> {
//        DownloadEventSelectionListener() {
//        }
//
//        @Override
//        public void componentSelected(final MenuEvent ce) {
//            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
//            currentModel = (TreeModel) selectedModel;
//            final Entity entity =  currentModel.getBaseEntity();
//
//            if (entity.getEntityType().equals(EntityType.point)) {
//                showDownloadPanel(entity);
//            }
//
//        }
//    }


    private class DumpEventSelectionListener extends SelectionListener<MenuEvent> {
        DumpEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity =  currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.point)) {
                showDumpPanel(entity);
            }

        }
    }

    private class UploadEventSelectionListener extends SelectionListener<MenuEvent> {
        UploadEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity =  currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.point)) {
                showUploadPanel(entity);
            }

        }
    }


}
