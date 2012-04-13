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

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.*;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.xmpp.*;
import com.nimbits.client.ui.helper.*;
import com.nimbits.client.ui.icons.*;
import com.nimbits.client.ui.panels.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/17/12
 * Time: 9:44 AM
 */
public class EntityContextMenu extends Menu {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private EntityTree<ModelData> tree;
    private TreeModel currentModel;
    private static final String PARAM_DEFAULT_WINDOW_OPTIONS = "menubar=no," +
            "location=false," +
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
    private MenuItem xmppContext;
    private MenuItem summaryContext;
    private MenuItem intelligenceContext;
    private Map<SettingType, String> settings;
    private final User user;

    private List<EntityModifiedListener> entityModifiedListeners;

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    private void notifyEntityModifiedListener(final TreeModel model, final Action action) throws NimbitsException {
        for (EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }

    public interface EntityModifiedListener {
        void onEntityModified(final TreeModel model, final Action action) throws NimbitsException;

    }


    public EntityContextMenu(final User user, final EntityTree<ModelData> tree, final Map<SettingType, String> settings) {
        super();
        this.user = user;
        entityModifiedListeners = new ArrayList<EntityModifiedListener>(1);
        this.tree = tree;
        this.settings = settings;
        deleteContext = deleteContext();
        subscribeContext = subscribeContext();
        reportContext = reportContext();
        MenuItem propertyContext = propertyContext();
        copyContext = copyContext();
        calcContext = calcContext();
        intelligenceContext = intelligenceContext();
        xmppContext = xmppResourceContext();
        summaryContext = summaryContext();
        add(propertyContext);
        add(copyContext);
        add(deleteContext);

        add(subscribeContext);
        add(reportContext);
        add(calcContext);
        add(summaryContext);
        add(xmppContext);
        if (settings.containsKey(SettingType.wolframKey) && ! Utils.isEmptyString(settings.get(SettingType.wolframKey))) {
            add(intelligenceContext);
        }



    }

    @Override
    public void showAt(int x, int y) {
        super.showAt(x, y);
        ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
        currentModel = (TreeModel)selectedModel;
        deleteContext.setEnabled(!currentModel.getEntityType().equals(EntityType.user) || ! currentModel.isReadOnly());
        subscribeContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) ||currentModel.getEntityType().equals(EntityType.category));
        reportContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.category));
        copyContext.setEnabled(currentModel.getEntityType().equals(EntityType.point));
        calcContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.calculation));
        intelligenceContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.intelligence));
        xmppContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.resource));
        summaryContext.setEnabled(currentModel.getEntityType().equals(EntityType.point) || currentModel.getEntityType().equals(EntityType.summary));


        propertyContext().setEnabled(!currentModel.isReadOnly());


    }

    private MenuItem deleteContext() {
        MenuItem retObj = new MenuItem();


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

    private MenuItem xmppResourceContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText("XMPP Resource Assignment");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.filter()));
        retObj.addSelectionListener(new XmppMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem summaryContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("New Summary Point");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.summary()));
        retObj.addSelectionListener(new SummaryMenuEventSelectionListener());
        return retObj;

    }

    private MenuItem intelligenceContext() {
        final MenuItem retObj = new MenuItem();

        retObj.setText("Intelligence");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connect()));
        retObj.addSelectionListener(new IntelligenceMenuEventSelectionListener());
        return retObj;
    }

    public void showIntelligencePanel(Entity entity) throws NimbitsException {
        IntelligencePanel dp = new IntelligencePanel(entity);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        if (entity.getEntityType().equals(EntityType.point)) {
            w.setHeading("Intelligence triggered when data is recorded to " + entity.getName().getValue());
        }
        else {
            w.setHeading("Edit Intelligence");

        }
        w.add(dp);
        dp.addEntityAddedListener(new IntelligenceEntityAddedListener(w));

        w.show();
    }

    public void showSummaryPanel(Entity entity) {
        SummaryPanel dp = new SummaryPanel(entity);
        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        w.setHeading("Summary");
        w.add(dp);
        dp.addEntityAddedListener(new SummaryEntityAddedListener(w));

        w.show();
    }

    private MenuItem propertyContext() {
        MenuItem retObj = new MenuItem();

        retObj.setText("Edit");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.edit()));
        retObj.addSelectionListener(new EditMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem subscribeContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Subscribe");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.plugin()));
        retObj.addSelectionListener(new SubscribeMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem copyContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Copy");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));
        retObj.addSelectionListener(new CopyMenuEventSelectionListener());
        return retObj;
    }

    private MenuItem reportContext() {
        MenuItem retObj = new MenuItem();
        retObj.setText("Report");
        retObj.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.form()));
        retObj.addSelectionListener(new ReportMenuEventSelectionListener());

        return retObj;
    }

    private final Listener<MessageBoxEvent> deleteEntityListener = new DeleteMessageBoxEventListener();

    private final Listener<MessageBoxEvent> copyPointListener  = new CopyPointMessageBoxEventListener();

    private final Listener<MessageBoxEvent> xmppResourceListener = new XMPPMessageBoxEventListener();

    public void showSubscriptionPanel(final Entity entity) {
        final SubscriptionPanel dp = new SubscriptionPanel(entity, settings);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        w.setHeading("Subscribe");
        w.add(dp);
        dp.addEntityAddedListener(new SubscribeEntityAddedListener(w));

        w.show();
    }

    public void showCalcPanel(final Entity entity) throws NimbitsException {
        CalculationPanel dp = new CalculationPanel(user, entity);

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        w.setWidth(WIDTH);
        w.setHeight(HEIGHT);
        if (entity.getEntityType().equals(EntityType.point)) {
            w.setHeading("Calculations triggered when data is recorded to " + entity.getName().getValue());
        }
        else {
            w.setHeading("Edit Calculation");

        }
        w.add(dp);
        dp.addEntityAddedListener(new EntityAddedListener(w));

        w.show();
    }

    private class EntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        EntityAddedListener(Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(Entity entity) throws NimbitsException {
            w.hide();
            notifyEntityModifiedListener(new GxtModel(entity), Action.create);

        }
    }

    private class DeleteMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        DeleteMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
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
        public void componentSelected(MenuEvent ce) {
            TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            Entity entity = selectedModel.getBaseEntity();
            try {
                showCalcPanel(entity);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }

    }

    private class CreateXMPPEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        CreateXMPPEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            box.close();
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(Entity result) {
            try {
                notifyEntityModifiedListener(new GxtModel(result), Action.create);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
            box.close();
        }
    }

    private class XMPPMessageBoxEventListener implements Listener<MessageBoxEvent> {
        private String newEntityName;

        XMPPMessageBoxEventListener() {
        }


        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your new XMPP Resource", "Creating: " + newEntityName);
                box.show();
                XMPPServiceAsync serviceAsync = GWT.create(XMPPService.class);
                EntityName name = null;
                try {
                    name = CommonFactoryLocator.getInstance().createName(newEntityName, EntityType.resource);
                } catch (NimbitsException caught) {
                    FeedbackHelper.showError(caught);
                }
                serviceAsync.createXmppResource(currentModel.getBaseEntity(), name, new CreateXMPPEntityAsyncCallback(box));

            }
        }
    }

    private class CopyEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        CopyEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            box.close();
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(Entity entity) {
            box.close();
            try {
                TreeModel model = new GxtModel(entity);
                notifyEntityModifiedListener(model, Action.create);
            } catch (NimbitsException e) {
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
                EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = null;
                try {
                    name = CommonFactoryLocator.getInstance().createName(newEntityName, EntityType.point);
                } catch (NimbitsException caught) {
                    FeedbackHelper.showError(caught);
                }
                Entity entity =  currentModel.getBaseEntity();

                service.copyEntity(entity, name, new CopyEntityAsyncCallback(box));

            }
        }
    }

    private class DeleteEntityAsyncCallback implements AsyncCallback<List<Entity>> {
        DeleteEntityAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(List<Entity> result) {
            try {
                notifyEntityModifiedListener(currentModel, Action.delete);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }
    }

    private class ReportMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        ReportMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            TreeModel model = (TreeModel) selectedModel;
            if (model.getEntityType().equals(EntityType.point) || model.getEntityType().equals(EntityType.category)) {
                Entity p =  model.getBaseEntity();
                try {
                    openUrl(p.getUUID(), p.getName().getValue());
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }
            }



        }

        private void openUrl(String uuid, String title) {
            String u = com.google.gwt.user.client.Window.Location.getHref()
                    + "?uuid=" + uuid
                    + "&count=10";
            com.google.gwt.user.client.Window.open(u, title, PARAM_DEFAULT_WINDOW_OPTIONS);
        }
    }

    private class SubscribeMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SubscribeMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            Entity entity =  currentModel.getBaseEntity();

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
        public void componentSelected(MenuEvent ce) {
            TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            Entity entity = selectedModel.getBaseEntity();

            try {
                switch (selectedModel.getEntityType()) {
                    case category:  {


                        CategoryPropertyPanel dp = new CategoryPropertyPanel(entity);
                        final Window w = new Window();
                        w.setWidth(WIDTH);
                        w.setHeight(HEIGHT);
                        try {
                            w.setHeading(entity.getName().getValue() + ' ' + Words.WORD_PROPERTIES);
                        } catch (NimbitsException e) {
                            FeedbackHelper.showError(e);
                        }
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
                    case intelligence:

                        showIntelligencePanel(entity);

                        break;
                    case summary:
                        showSummaryPanel(entity);
                        break;
                    case file:
                        FilePropertyPanel dp = new FilePropertyPanel(entity);
                        final Window w = new Window();
                        w.setWidth(WIDTH);
                        w.setHeight(HEIGHT);

                        w.setHeading(entity.getName().getValue() + ' ' + Words.WORD_PROPERTIES);

                        w.add(dp);
                        w.show();
                        break;
                }
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }

        private void createPointPropertyWindow(Entity entity) throws NimbitsException {
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
            public void onPointUpdated(Entity result) throws NimbitsException {
                notifyEntityModifiedListener(new GxtModel(result), Action.create);
            }
        }
    }

    private class SubscribeEntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        SubscribeEntityAddedListener(Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(Entity entity) throws NimbitsException {
            w.hide();
            Cookies.removeCookie(Action.subscribe.name());
            notifyEntityModifiedListener(new GxtModel(entity), Action.create);

        }
    }

    private class XmppMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        XmppMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;

            if (currentModel != null) {
                if (currentModel.getEntityType().equals(EntityType.point) && ! currentModel.isReadOnly()) {
                    final MessageBox box= MessageBox.prompt(
                            "New XMPP Resource Name",
                            "<p>When a subscription notification for this point occurs that is configured to broadcast an alert " +
                                    "over an XMPP instant message, the default transmission uses your bare account address. " +
                                    "</p><p>You can configure " +
                                    "a point to only send a message to a client listening to a specific resource " +
                                    "i.e <b>test@example.com/resourceName</b></p><BR>");
                    box.addCallback(xmppResourceListener);
                    box.show();
                }
            }


        }

    }

    private class IntelligenceEntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        IntelligenceEntityAddedListener(Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(Entity entity) throws NimbitsException {
            w.hide();
            notifyEntityModifiedListener(new GxtModel(entity), Action.create);

        }
    }

    private class DeleteMessageBoxEventListener implements Listener<MessageBoxEvent> {
        DeleteMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent ce) {
            com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();
            final EntityServiceAsync service = GWT.create(EntityService.class);

            if (btn.getText().equals(Words.WORD_YES)) {
                final Entity entityToDelete = currentModel.getBaseEntity();
                service.deleteEntity(entityToDelete, new DeleteEntityAsyncCallback());

            }
        }
    }

    private class CopyMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        CopyMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            final MessageBox box;
            if (currentModel.getEntityType().equals(EntityType.point) && ! currentModel.isReadOnly()) {
                box= MessageBox.prompt(
                        UserMessages.MESSAGE_NEW_POINT,
                        UserMessages.MESSAGE_NEW_POINT_PROMPT);
                box.addCallback(copyPointListener);
            }
            else {
                box = MessageBox.alert("Not supported", "Sorry, for the moment you can only copy your data points", null);
            }
            box.show();
        }
    }

    private class IntelligenceMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        IntelligenceMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            TreeModel selectedModel = (TreeModel) tree.getSelectionModel().getSelectedItem();
            Entity entity = selectedModel.getBaseEntity();
            try {
                showIntelligencePanel(entity);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }

    }

    private class SummaryEntityAddedListener implements NavigationEventProvider.EntityAddedListener {
        private final Window w;

        SummaryEntityAddedListener(Window w) {
            this.w = w;
        }

        @Override
        public void onEntityAdded(Entity entity) throws NimbitsException {
            w.hide();
            Cookies.removeCookie(Action.subscribe.name());
//                if (entity.getEntityType().equals(EntityType.point)) {
            notifyEntityModifiedListener(new GxtModel(entity), Action.create);
//                }


        }
    }

    private class SummaryMenuEventSelectionListener extends SelectionListener<MenuEvent> {
        SummaryMenuEventSelectionListener() {
        }

        @Override
        public void componentSelected(MenuEvent ce) {
            ModelData selectedModel = tree.getSelectionModel().getSelectedItem();
            currentModel = (TreeModel) selectedModel;
            Entity entity =  currentModel.getBaseEntity();

            if (entity.getEntityType().equals(EntityType.subscription)  ||
                    entity.getEntityType().equals(EntityType.point)) {
                showSummaryPanel(entity);
            }

        }
    }
}
