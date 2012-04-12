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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.Style.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.dnd.DND.*;
import com.extjs.gxt.ui.client.dnd.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.recordedvalues.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;


public class NavigationPanel extends NavigationEventProvider {

    private EntityTree<ModelData> tree;
     private Timer updater;
    private boolean expanded = false;
    private Map<SettingType, String> settings;
    private List<String> parents;
    private EntityContextMenu context;
    private final User user;
    private boolean saveWithCurrentTime = true;
    private final static int valueColumnIndex = 1;

    public NavigationPanel(final User user,
                           final Map<SettingType, String> settings) {

        this.settings = settings;
        this.user = user;
        setBorders(false);
        setScrollMode(Scroll.AUTO);
        tree = new EntityTree<ModelData>();

        getUserEntities(false);

    }

    public void toggleExpansion() {
        if (expanded) {
            tree.collapseAll();
            expanded = false;
        } else {
            tree.expandAll();
            expanded = true;
        }
    }

    private void updateModel(final Value value, final TreeModel model) {
        model.set(Parameters.value.getText(), value.getValueWithNote());
        model.set(Parameters.data.getText(), value.getData());
        model.set(Parameters.timestamp.getText(), value.getTimestamp());
        model.set(Parameters.note.getText(), value.getNote());
        model.setAlertType(value.getAlertState());
        model.setDirty(false);
        tree.getTreeStore().update(model);
        notifyValueEnteredListener(model, value);
    }

    public void addUpdateTreeModel(final TreeModel model, final boolean refresh) throws NimbitsException {

        if (tree != null && tree.getStore() != null) {

            final ModelData mx = tree.getTreeStore().findModel(Parameters.id.getText(), model.getBaseEntity().getKey());
            if (mx != null) {
                final TreeModel m = (TreeModel)mx;
                m.update(model.getBaseEntity());
                tree.getTreeStore().update(m);
                if (! refresh) {
                    tree.setExpanded(mx, true);
                }
            }
            else {
                final ModelData parent = tree.getTreeStore().findModel(Parameters.id.getText(), model.getBaseEntity().getParent());
                if (parent != null) {
                    tree.getTreeStore().add(parent, model, true);

                }
            }
            tree.setExpanded(model, true);
        }
    }

    @Override
    protected void onAttach() {
        updater = new RefreshTimer();
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    //service calls
    public void getUserEntities(final boolean refresh)  {


        final EntityServiceAsync service = GWT.create(EntityService.class);
        service.getEntities(new GetUserListAsyncCallback(refresh));

    }

    public void saveAll() {

        //  final List<GxtModel> models = grid.getSelectionModel().getSelectedItems();
        RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);

        for (final ModelData x :  tree.getTreeStore().findModels(Parameters.dirty.getText(), "yes")) {
            final TreeModel model = (TreeModel)x;
            Date date = model.get(Parameters.timestamp.getText()) == null ? new Date() : (Date) model.get(Parameters.timestamp.getText());
            final Date timestamp = saveWithCurrentTime ? new Date() : date;
            final String v = model.get(Parameters.value.getText());
//            final String note = model.get(Const.Params.PARAM_NOTE);
//            final String data = model.get(Const.PARAM_DATA);
            final Value value = ValueModelFactory.createValueModel(v, timestamp, model.getId());

            service.recordValue(model.getBaseEntity(), value, new SaveValueAsyncCallback(model));
            model.setDirty(false);
        }
        tree.getTreeStore().commitChanges();



    }

    private static class MoveEntityAsyncCallback implements AsyncCallback<Entity> {
        MoveEntityAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(Entity result) {

        }
    }

    private class SaveValueAsyncCallback implements AsyncCallback<Value> {
        private final TreeModel model;

        SaveValueAsyncCallback(TreeModel model) {
            this.model = model;
        }

        @Override
        public void onFailure(final Throwable throwable) {

            GWT.log(throwable.getMessage(), throwable);
        }

        @Override
        public void onSuccess(final Value aValue) {
            updateModel(aValue, model);

        }
    }

    private class GetUserListAsyncCallback implements AsyncCallback<List<Entity>> {
        private final boolean refresh;

        GetUserListAsyncCallback(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(List<Entity> result) {

            try {
            if (refresh) {
                for (Entity e : result) {


                        addUpdateTreeModel(new GxtModel(e), true);


                }
            }
            else {

                    createTree(result);

                doLayout();
            }
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }
        private void createTree(final List<Entity> result) throws NimbitsException {
            final TreeGridDropTarget target = new TreeGridDropTarget(tree);
            target.setAllowSelfAsSource(true);
            target.setFeedback(Feedback.BOTH);
            tree.addListener(Events.AfterEdit, new GridEventListener());
            treePropertyBuilder();
            treeStoreBuilder(result);
            treeDNDBuilder();
            removeAll();
            add(tree);
        }
        private void treeDNDBuilder() {
            TreeGridDragSource source = new TreeGridDragSource(tree);
            source.addDNDListener(new DNDListener());
        }
        private TreeModel treeStoreBuilder(final List<Entity> result) throws NimbitsException {

            final List<ModelData> model = new ArrayList<ModelData>(result.size());
            parents = new ArrayList<String>(result.size());
            for (final Entity entity : result) {
                addEntity(entity);

            }

            final TreeModel userModel = new GxtModel(user);

            addChildrenToModel(result, parents, userModel);

            model.add(userModel);

            tree.getTreeStore().add(model, true);

            return userModel;

        }
        private void addEntity(final Entity entity) {
            if (! Utils.isEmptyString(entity.getParent()) && ! parents.contains(entity.getParent())) {
                parents.add(entity.getParent());
            }

        }
        private void addChildrenToModel(final List<Entity> result, List<String> parents, TreeModel model) throws NimbitsException {


            for (final Entity entity : result) {
                if (! entity.getEntityType().equals(EntityType.user)) {// entity.getEntity().equals(this.user.getKey()) ) {
                    if (entity.getParent().equals(model.getUUID())) {
                        TreeModel model2 = new GxtModel(entity);
                        if (parents.contains(entity.getKey()) && ! entity.getEntityType().equals(EntityType.user)) {
                            addChildrenToModel(result, parents, model2);
                        }

                        model.add(model2);
                    }
                }
            }
            // return model;
        }
        private void treePropertyBuilder() {
            context = new EntityContextMenu(user, tree, settings);
            context.addEntityModifiedListeners(new EntityModifiedListener());
            tree.setContextMenu(context);
            tree.setStateful(true);

            tree.setClicksToEdit(EditorGrid.ClicksToEdit.ONE);
            tree.setTrackMouseOver(true);
            //tree.getView().setAutoFill(true);
            tree.addListener(Events.RowDoubleClick, new TreeDoubleClickGridEventListener());

        }


        private class EntityModifiedListener implements EntityContextMenu.EntityModifiedListener {
            EntityModifiedListener() {
            }

            @Override
            public void onEntityModified(TreeModel model, Action action) throws NimbitsException {
                switch (action) {
                    case delete:
                        removeEntity(model);
                        break;
                    case update: case create:
                        addUpdateTreeModel(model, false);
                        break;
                    default:
                        break;
                }
            }
            private void removeEntity(TreeModel currentModel) {

                if (tree != null && tree.getStore() != null) {


                    ModelData m =  tree.getTreeStore().findModel(Parameters.id.getText(), currentModel.getBaseEntity().getKey());
                    tree.getTreeStore().remove(m);

                }
            }
        }

        private class TreeDoubleClickGridEventListener implements Listener<TreeGridEvent<ModelData>> {
            TreeDoubleClickGridEventListener() {
            }

            @Override
            public void handleEvent(TreeGridEvent<ModelData> be) {
                ModelData mx = tree.getSelectionModel()
                        .getSelectedItem();

                if (mx != null) {
                    TreeModel model = (TreeModel) mx;

                    try {
                        switch (model.getBaseEntity().getEntityType()) {
                            case user:
                                break;
                            case point:
                                break;
                            case category:
                                break;
                            case file:
                                break;
                            case subscription:
                                break;
                            case feed:
                                notifyEntityClickedListener(model);
                                break;
                            case userConnection:
                                break;
                            case calculation:
                                context.showCalcPanel(model.getBaseEntity());
                                break;
                            case summary:
                                context.showSummaryPanel(model.getBaseEntity());
                                break;
                            case intelligence:
                                context.showIntelligencePanel(model.getBaseEntity());
                                break;
                            case resource:
                                break;
                            case instance:
                                break;
                        }
                    }
                    catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }



                }
            }

        }
    }

    private class RecordValueCallback implements AsyncCallback<Value> {
        private final GridEvent be;
        private final TreeModel model;

        RecordValueCallback(GridEvent be, TreeModel model) {
            this.be = be;
            this.model = model;
        }

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
    }

    private class ReloadAsyncCallback implements AsyncCallback<Map<String, Entity>> {

        ReloadAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable throwable) {
            GWT.log(throwable.getMessage(), throwable);
        }

        @Override
        public void onSuccess(Map<String, Entity> stringPointMap) {
            final TreeStore<ModelData> models = tree.getTreeStore();
            for (final ModelData m : models.getAllItems()) {
                final TreeModel model = (TreeModel) m;
                if (!model.isDirty() && model.getEntityType().equals(EntityType.point)) {

                    if (stringPointMap.containsKey(model.getUUID())) {
                        Point p = (Point) stringPointMap.get(model.getUUID());
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
    }

    private class DNDListener extends com.extjs.gxt.ui.client.event.DNDListener {
        ModelData selectedModel;

        DNDListener() {
        }

        @Override
        public void dragStart(DNDEvent e) {
            super.dragStart(e);
            selectedModel = tree.getSelectionModel().getSelectedItem();
            TreeModel treeModel = (TreeModel)selectedModel;

            e.setCancelled(  treeModel.isReadOnly());
            e.getStatus().setStatus(  ! treeModel.isReadOnly());

        }

        @Override
        public void dragDrop(final DNDEvent e) {
            super.dragDrop(e);
            if (!e.getTarget().getInnerHTML().equals("&nbsp;")) {
                if (selectedModel instanceof TreeModel) {
                    final TreeModel model = (TreeModel) selectedModel;
                    selectedModel.set(Parameters.name.getText(), model.getName().getValue());
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
        private Entity getDropTarget(String targetName) {

            ModelData modelData = tree.getTreeStore().findModel(Parameters.name.getText(), targetName);
            return ((TreeModel) modelData).getBaseEntity();


        }
        private void moveEntity(Entity draggedEntity, Entity target) {

            if ( target.getOwner().equals(draggedEntity.getOwner())) {

                EntityServiceAsync service = GWT.create(EntityService.class);
                draggedEntity.setParent(target.getKey());

                service.addUpdateEntity(draggedEntity, new MoveEntityAsyncCallback());
            }
        }

    }

    private class GridEventListener implements Listener<GridEvent> {

        GridEventListener() {
        }

        @Override
        public void handleEvent(final GridEvent be) {
            final TreeModel model = (TreeModel) be.getModel();

            if (be.getColIndex() == valueColumnIndex) { //only save when the value is updated

                if (!model.isReadOnly()) {
                    model.setDirty(true);


                    final Entity entity =model.getBaseEntity();
                    Date timestamp = saveWithCurrentTime ? new Date() : (Date) model.get(Parameters.timestamp.getText());


                    final String valueAndNote = model.get(Parameters.value.getText());
                    //final String data = model.get(Const.PARAM_DATA);
                    String uuid = model.getId();
                    if (timestamp == null) {
                        timestamp = new Date();
                    }

                    final Value value = ValueModelFactory.createValueModel(valueAndNote, timestamp, uuid);

                    RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
                    service.recordValue(entity, value, new RecordValueCallback(be, model));

                }
            }
            else {
                model.setDirty(true);
            }
        }
    }

    private class RefreshTimer extends Timer {
        RefreshTimer() {
        }

        @Override
        public void run() {

            if (tree != null) {

                reloadCurrentValues( getVisiblePoints());
            }

        }
        private void reloadCurrentValues(final Map<String, Point> entityMap) {
            final RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
            service.getCurrentValues(entityMap, new ReloadAsyncCallback());
        }

        private Map<String, Point> getVisiblePoints() {
            final Map<String, Point> entityMap = new HashMap<String, Point>(tree.getTreeStore().getAllItems().size());

            if (tree != null) {
                for (final ModelData m : tree.getTreeStore().getAllItems()) {
                    final TreeModel model = (TreeModel) m;
                    try {
                        if (model != null
                                && model.getParent() != null
                                && !model.isDirty()
                                && model.getEntityType().equals(EntityType.point)
                                )  {
                            if (tree.isExpanded(model.getParent())) {
                                entityMap.put(model.getUUID(), (Point) model.getBaseEntity());
                            }
                        }
                    } catch (Exception e) {
                        GWT.log(e.getMessage(), e);
                    }
                }
            }
            return entityMap;
        }
    }
}