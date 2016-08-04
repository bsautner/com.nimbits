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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.controls.menu.AddFolderMenuItem;
import com.nimbits.client.ui.controls.menu.AddPointMenuItem;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;
import com.nimbits.client.ui.panels.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EntityContextMenu extends Menu implements BasePanel.PanelEvent {
    private final Logger logger = Logger.getLogger(EntityContextMenu.class.getName());

    private static final String SCHEDULE_TIMER = "Schedule Timer";
    private static final String CREATE_CALCULATION = "Create Calculation";
    private static final String CREATE_WEBHOOK = "Create Web Hook";
    private static final String SYNCHRONIZE_POINTS = "Synchronize Points";
    private static final String EDIT_PROPERTIES = "Edit Properties";
    private static final String COMPUTE_STATISTICS = "Compute Statistics";
    private static final String SET_ALERTS = "Set Alerts";
    private static final String SUBSCRIBE_TO_EVENTS1 = "Subscribe To Events";
    private static final String COPY_ENTITY = "Copy Entity";
    private static final String DELETE = "Delete";
    private static final String OUTBOUND_SOCKET = "Outbound Socket";
    private static final String SUMMARIZE = "Summarize";
    private static final String EXTERNAL_WEB_HOOK = "External Web Hook";
    private static final String SUBSCRIBE_TO_EVENTS = "Subscribe to Events";
    private static final String SCHEDULE = "Create a Schedule Timer";
    private static final String MESSAGE_ADD_CATEGORY = "Add a new data point Category";

    private static final String MESSAGE_NEW_POINT_PROMPT = "Please enter the name of the new data point.";

    private static final String PLEASE_SELECT_A_PARENT = "Please select a parent. You may not have clicked on the item in the tree you're creating a new point under, if you're a new user, try clicking your email address first.";
    private final Listener<MessageBoxEvent> createNewPointListener = new NewPointMessageBoxEventListener();
    private final Listener<MessageBoxEvent> createNewFolderListener = new NewFolderMessageBoxEventListener();

    private final Listener<MessageBoxEvent> deleteEntityListener = new DeleteMessageBoxEventListener();
    private final Listener<MessageBoxEvent> copyPointListener = new CopyPointMessageBoxEventListener();
    private BasePanel panel;
    private com.extjs.gxt.ui.client.widget.Window w;
    private static final String MESSAGE_NEW_POINT = "New Data Point";


    private EntityTree<ModelData> tree;
    private TreeModel currentModel;

    private MenuItem syncContext;
    private MenuItem deleteContext;
    private MenuItem subscribeContext;

    private MenuItem copyContext;
    private MenuItem calcContext;
    private MenuItem summaryContext;
    private MenuItem propertyContext;
    private MenuItem alertContext;

    private MenuItem webhookContext;

    private MenuItem scheduleContext;

    private AddPointMenuItem addPointMenuItem;
    private AddFolderMenuItem addFolderMenuItem;
    private MenuItem socketContext;

    private final User user;

    private List<EntityModifiedListener> entityModifiedListeners;

    private class NewPointMessageBoxEventListener implements Listener<MessageBoxEvent> {

        private String newEntityName;

        NewPointMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {


                final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
                currentModel = (TreeModel) selectedModel;
                if (currentModel != null) {
                    final MessageBox box = MessageBox.wait("Progress",
                            "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                    box.show();
                    EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);

                    final Entity currentEntity = currentModel.getBaseEntity();
                    try {
                        EntityName name = CommonFactory.createName(newEntityName, EntityType.point);

                        Point p = new PointModel.Builder().name(name).parent(currentEntity.getId()).create();

                        service.addUpdateEntityRpc(p, new NewPointEntityAsyncCallback(box));
                    } catch (Exception e) {
                        box.close();
                        FeedbackHelper.showError(e);
                    }


                } else {
                    FeedbackHelper.showError(new Exception(PLEASE_SELECT_A_PARENT));
                }


            }
        }


        private class NewPointEntityAsyncCallback implements AsyncCallback<Entity> {
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
            public void onSuccess(Entity result) {

                try {
                    notifyEntityModifiedListener(new GxtModel(result), Action.create);
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
                currentModel = (TreeModel) selectedModel;
                if (currentModel != null) {
                    final MessageBox box = MessageBox.wait("Progress",
                            "Creating your folder", "Creating: " + newEntityName);
                    box.show();
                    EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);

                    final Entity currentEntity = currentModel.getBaseEntity();
                    try {
                        EntityName name = CommonFactory.createName(newEntityName, EntityType.category);
                        Category p = new CategoryModel.Builder().name(name).create();
                        p.setParent(currentEntity.getId());
                        service.addUpdateEntityRpc(p, new NewFolderEntityAsyncCallback(box));
                    } catch (Exception e) {
                        box.close();
                        FeedbackHelper.showError(e);
                    }


                } else {
                    FeedbackHelper.showError(new Exception("Please select a parent"));
                }


            }
        }


        private class NewFolderEntityAsyncCallback implements AsyncCallback<Entity> {
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
            public void onSuccess(Entity result) {

                try {
                    notifyEntityModifiedListener(new GxtModel(result), Action.create);
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

                box.close();
            }
        }
    }


    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    private void notifyEntityModifiedListener(final TreeModel model, final Action action) {
        for (final EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }

    public interface EntityModifiedListener {
        void onEntityModified(final TreeModel model, final Action action);

    }

    private class NewPointBaseEventListener implements Listener<BaseEvent> {
        NewPointBaseEventListener() {
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            final MessageBox box = MessageBox.prompt(
                    MESSAGE_NEW_POINT,
                    MESSAGE_NEW_POINT_PROMPT);

            box.addCallback(createNewPointListener);
        }
    }

    private class NewFolderBaseEventListener implements Listener<BaseEvent> {
        NewFolderBaseEventListener() {
        }

        @Override
        public void handleEvent(final BaseEvent be) {
            final MessageBox box = MessageBox.prompt(
                    MESSAGE_ADD_CATEGORY,
                    MESSAGE_ADD_CATEGORY);

            box.addCallback(createNewFolderListener);
        }
    }

    private class SocketMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SocketMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            final Entity entity = selectedModel.getBaseEntity();
            try {
                showSocketPanel(entity);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }

    }


    public EntityContextMenu(final User user, final EntityTree<ModelData> tree) {
        super();
        this.user = user;
        this.tree = tree;

        this.propertyContext = propertyContext();
        this.alertContext = alertContext();
        this.addPointMenuItem = new AddPointMenuItem();
        this.addFolderMenuItem = new AddFolderMenuItem();
        this.addPointMenuItem.addListener(Events.OnClick, new NewPointBaseEventListener());
        this.addFolderMenuItem.addListener(Events.OnClick, new NewFolderBaseEventListener());
        this.entityModifiedListeners = new ArrayList<EntityModifiedListener>(1);
        this.syncContext = syncContext();
        this.deleteContext = deleteContext();
        this.subscribeContext = subscribeContext();
        this.webhookContext = webhookContext();

        copyContext = copyContext();
        calcContext = calcContext();
        socketContext = socketContext();
        summaryContext = summaryContext();


        scheduleContext = scheduleContext();

        add(addPointMenuItem);
        add(addFolderMenuItem);
        add(socketContext);
        add(propertyContext);
        add(alertContext);
        add(syncContext);

        add(scheduleContext);

        add(copyContext);
        add(deleteContext);
        add(subscribeContext);

        add(calcContext);
        add(summaryContext);
        add(webhookContext);
        //TODO - avoid infinite recursion with hooks - feature flag for client
        //options for as query string or path


    }


    @Override
    public void showAt(final int x, final int y) {
        super.showAt(x, y);
        final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
        currentModel = (TreeModel) selectedModel;
        deleteContext.setEnabled(!currentModel.getEntityType().equals(EntityType.user) || !currentModel.isReadOnly());
        subscribeContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.category));

        copyContext.setEnabled(currentModel.getEntityType().equals(EntityType.point));
        calcContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.calculation));
        summaryContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.summary));
        syncContext.setEnabled(!currentModel.getEntityType().equals(EntityType.point) || !currentModel.isReadOnly());

        propertyContext.setEnabled(!currentModel.isReadOnly() && currentModel.getEntityType().equals(EntityType.point));
        alertContext.setEnabled(!currentModel.isReadOnly() && currentModel.getEntityType().equals(EntityType.point));

        syncContext.setEnabled(currentModel.getEntityType().equals(EntityType.point));

    }


    private MenuItem syncContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(SYNCHRONIZE_POINTS);
        retObj.setIcon((Icons.INSTANCE.connection()));
        retObj.addSelectionListener(new SyncMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem deleteContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(DELETE);
        retObj.setIcon((Icons.INSTANCE.delete()));
        retObj.addSelectionListener(new DeleteMenuEventSelectionListener());
        return retObj;
    }


    private MenuItem webhookContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(EXTERNAL_WEB_HOOK);
        retObj.setIcon((Icons.INSTANCE.webhook()));
        retObj.addSelectionListener(new WebHookEventSelectionListener());
        return retObj;
    }

    private MenuItem calcContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText(CREATE_CALCULATION);
        retObj.setIcon((Icons.INSTANCE.formula()));
        retObj.addSelectionListener(new CalcMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem socketContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText(OUTBOUND_SOCKET);
        retObj.setIcon((Icons.INSTANCE.socket()));
        retObj.addSelectionListener(new SocketMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem summaryContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(SUMMARIZE);
        retObj.setIcon((Icons.INSTANCE.expand()));
        retObj.addSelectionListener(new SummaryMenuEventSelectionListener());
        return retObj;

    }


    private MenuItem scheduleContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText(SCHEDULE);

        retObj.setIcon((Icons.INSTANCE.schedule()));
        retObj.addSelectionListener(new ScheduleEventSelectionListener());
        return retObj;

    }


    private MenuItem propertyContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText(EDIT_PROPERTIES);
        retObj.setIcon((Icons.INSTANCE.edit()));
        retObj.addSelectionListener(new EditMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem alertContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText(SET_ALERTS);
        retObj.setIcon((Icons.INSTANCE.Warning()));
        retObj.addSelectionListener(new AlertEditSelectionListener());
        return retObj;
    }

    private MenuItem subscribeContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(SUBSCRIBE_TO_EVENTS1);
        retObj.setIcon((Icons.INSTANCE.plugin()));
        retObj.addSelectionListener(new SubscribeMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem copyContext() {
        final MenuItem retObj = new MenuItem();
        retObj.setText(COPY_ENTITY);
        retObj.setIcon((Icons.INSTANCE.album()));
        retObj.addSelectionListener(new CopyMenuEventSelectionListener());
        return retObj;
    }

    private void showModal(BasePanel panel, String heading) {
        panel.addEntityAddedListener(new EntityAddedListener());
        close();
        w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(BasePanel.WIDTH);
        w.setHeight(BasePanel.HEIGHT);
        w.setHeadingText(heading);
        w.add(panel);

        w.show();
    }

    @Override
    public void close() {
        logger.info("Closing Modal");
        if (w != null && w.isClosable() && w.isVisible()) {
            logger.info("hiding...");
            w.hide();
        }
    }


    public void showChartPanel(final Entity entity) {
        final ChartPanel dp = new ChartPanel(user, entity);

        w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(800);
        w.setHeight(500);
        w.setHeadingText(entity.getName().getValue());
        w.add(dp);


        w.show();

    }

    public void showSubscriptionPanel(final Entity entity) {
        final SubscriptionPanel panel = new SubscriptionPanel(this, entity, user);
        showModal(panel, SUBSCRIBE_TO_EVENTS);

    }

    public void showCalcPanel(final Entity entity) {
        final CalculationPanel panel = new CalculationPanel(this, entity);
        showModal(panel, CREATE_CALCULATION);

    }

    public void showWebHookPanel(final Entity entity) {
        final WebHookPanel panel = new WebHookPanel(this, entity);
        showModal(panel, CREATE_WEBHOOK);

    }

    public void showSchedulePanel(final Entity entity) {
        SchedulePanel panel = new SchedulePanel(this, entity);
        showModal(panel, SCHEDULE_TIMER);


    }

    public void showSocketPanel(final Entity entity) {
        panel = new SocketPanel(this, entity);
        showModal(panel, SYNCHRONIZE_POINTS);

    }


    public void showPointPanel(final Entity entity) {
        panel = new PointPanel(this, entity);
        showModal(panel, EDIT_PROPERTIES);

    }

    public void showSummaryPanel(final Entity entity) {
        panel = new SummaryPanel(this, entity);
        showModal(panel, COMPUTE_STATISTICS);

    }

    private void showSyncPanel(final Entity entity) {
        panel = new SyncPanel(this, entity);
        showModal(panel, SYNCHRONIZE_POINTS);
    }


    private void showAlertPanel(final Entity entity) {
        AlertPanel panel = new AlertPanel(this, entity);
        showModal(panel, SET_ALERTS);

    }


    private class EntityAddedListener implements NavigationEventProvider.EntityAddedListener {


        EntityAddedListener() {

        }

        @Override
        public void onEntityAdded(final Entity entity) {
            logger.info("entity added");
            close();
            notifyEntityModifiedListener(new GxtModel(entity), Action.create);

        }
    }

    private class DeleteMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        DeleteMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            if (!currentModel.isReadOnly()) {
                MessageBox.confirm("Confirm", "Are you sure you want delete this? Doing so will permanently delete it including all of it's children (points, documents data etc)"
                        , deleteEntityListener);
            }

        }
    }

    private class WebHookEventSelectionListener extends SelectionListener<MenuEvent> {
        WebHookEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            final Entity entity = selectedModel.getBaseEntity();
            try {
                showWebHookPanel(entity);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
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

    private class SyncMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SyncMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity = currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.point) || entity.getEntityType().equals(EntityType.user)) {
                showSyncPanel(entity);
            }

        }
    }

    private class CopyEntityAsyncCallback implements AsyncCallback<Entity> {
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
        public void onSuccess(final Entity entity) {
            box.close();
            try {
                final TreeModel model = new GxtModel(entity);
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
                final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
                EntityName name = null;
                try {
                    name = CommonFactory.createName(newEntityName, EntityType.point);
                } catch (Exception caught) {
                    FeedbackHelper.showError(caught);
                }
                final Entity entity = currentModel.getBaseEntity();

                service.copyEntity(entity, name, new CopyEntityAsyncCallback(box));

            }
        }
    }

    private class DeleteEntityAsyncCallback implements AsyncCallback<Void> {
        DeleteEntityAsyncCallback() {
        }

        @Override
        public void onFailure(final Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(Void result) {
            try {
                notifyEntityModifiedListener(currentModel, Action.delete);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }

        }
    }

    private class SubscribeMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SubscribeMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity = currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.subscription) ||
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

                    case point:


                        showPointPanel(entity);


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


    }

    private class DeleteMessageBoxEventListener implements Listener<MessageBoxEvent> {
        DeleteMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent ce) {
            final com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();
            final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);

            if (btn.getHtml().equalsIgnoreCase("YES")) {
                final Entity entityToDelete = currentModel.getBaseEntity();
                service.deleteEntityRpc(entityToDelete, new DeleteEntityAsyncCallback());

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
            if (currentModel.getEntityType().equals(EntityType.point) && !currentModel.isReadOnly()) {
                box = MessageBox.prompt(
                        MESSAGE_NEW_POINT,
                        MESSAGE_NEW_POINT_PROMPT);
                box.addCallback(copyPointListener);
            } else {
                box = MessageBox.alert("Not supported", "Sorry, for the moment you can only copy your data points", null);
            }
            box.show();
        }
    }

    private class SummaryMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SummaryMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity = currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.subscription) ||
                    entity.getEntityType().equals(EntityType.point)) {

                showSummaryPanel(entity);
            }


        }
    }


    private class AlertEditSelectionListener extends SelectionListener<MenuEvent> {
        AlertEditSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity = currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.point)) {
                showAlertPanel(entity);
            }

        }
    }


    private class ScheduleEventSelectionListener extends SelectionListener<MenuEvent> {
        ScheduleEventSelectionListener() {
        }

        @Override
        public void componentSelected(final MenuEvent ce) {
            final ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final Entity entity = currentModel.getBaseEntity();
            showSchedulePanel(entity);


        }
    }


}
