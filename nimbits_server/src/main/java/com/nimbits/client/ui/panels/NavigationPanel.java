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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;

import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.service.value.ValueServiceRpc;
import com.nimbits.client.service.value.ValueServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityContextMenu;
import com.nimbits.client.ui.controls.EntityTree;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.*;


class NavigationPanel extends NavigationEventProvider {

    private EntityTree<ModelData> tree;
    private Timer updater;

    private List<String> parents;
    private EntityContextMenu context;
    private final User user;
    private final static int valueColumnIndex = 1;
    private final ValueServiceRpcAsync valueService;
    private final EntityServiceRpcAsync entityService;

    NavigationPanel(final User user) {


        this.user = user;
        this.tree = new EntityTree<ModelData>();
        this.valueService = GWT.create(ValueServiceRpc.class);
        this.entityService = GWT.create(EntityServiceRpc.class);
        setBorders(true);
        setScrollMode(Scroll.ALWAYS);


    }

    @Override
    protected void onLoad() {
        super.onLoad();

        getUserEntities();

    }

    private void updateModel(final Value value, final TreeModel model) {
        model.set(Parameters.value.getText(), value.getValueWithData());
        model.set(Parameters.data.getText(), value.getData());
        model.set(Parameters.timestamp.getText(), new Date(value.getLTimestamp()));

        model.setAlertType(value.getAlertState());
        model.setDirty(false);
        tree.getTreeStore().update(model);
        notifyValueEnteredListener(model, value);
    }

    private void addUpdateTreeModel(final TreeModel model, final boolean refresh) {

        if (tree != null && tree.getStore() != null) {

            final ModelData mx = tree.getTreeStore().findModel(Parameters.id.getText(), model.getBaseEntity().getId());
            if (mx != null) {
                final TreeModel m = (TreeModel) mx;
                m.update(model.getBaseEntity());
                tree.getTreeStore().update(m);
                if (!refresh) {
                    tree.setExpanded(mx, true);
                }
            } else {
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
        super.onAttach();
        updater = new RefreshTimer();
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();

    }

    //service calls
    private void getUserEntities() {

        entityService.getEntitiesRpc(user, new GetUserListAsyncCallback());

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

    private class SaveValueAsyncCallback implements AsyncCallback<Void> {
        private final TreeModel model;
        private final Value value;

        SaveValueAsyncCallback(TreeModel model, Value value) {
            this.model = model;
            this.value = value;
        }

        @Override
        public void onFailure(final Throwable throwable) {

            GWT.log(throwable.getMessage(), throwable);
        }

        @Override
        public void onSuccess(final Void object) {
            updateModel(value, model);

        }
    }

    private class GetUserListAsyncCallback implements AsyncCallback<List<Entity>> {

        GetUserListAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
            GWT.log(caught.getMessage(), caught);

        }

        @Override
        public void onSuccess(List<Entity> result) {



            reloadTree(result);



        }

        private void reloadTree(List<Entity> result) {
            createTree(result);

            doLayout();

        }

        private void createTree(final List<Entity> result) {

            final TreeGridDropTarget target = new TreeGridDropTarget(tree);
            if (tree.getTreeStore() != null) {
                tree.getTreeStore().removeAll();
            }
            target.setAllowSelfAsSource(true);
            target.setFeedback(Feedback.BOTH);
            tree.addListener(Events.AfterEdit, new GridEventListener());
            tree.addListener(Events.BeforeEdit, new GridBeforeEditEventListener());
            tree.addListener(Events.Expand, new GridExpandListener());
            tree.addListener(Events.OnMouseOver, new GridHoverListener());
            treePropertyBuilder();
            TreeModel top = treeStoreBuilder(result);
            treeDNDBuilder();
            removeAll();

            add(tree);
            tree.setExpanded(top, true);


        }

        private void treeDNDBuilder() {
            TreeGridDragSource source = new TreeGridDragSource(tree);
            source.addDNDListener(new DNDListener());
        }

        private TreeModel treeStoreBuilder(final List<Entity> result) {

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
            if (!Utils.isEmptyString(entity.getParent()) && !parents.contains(entity.getParent())) {
                parents.add(entity.getParent());
            }

        }

        private void addChildrenToModel(final List<Entity> result, List<String> parents, TreeModel model) {


            for (final Entity entity : result) {
                if (!entity.getEntityType().equals(EntityType.user)) {// entity.getEntity().equals(this.user.getId()) ) {
                    if (entity.getParent().equals(model.getId())) {
                        TreeModel model2 = new GxtModel(entity);
                        if (parents.contains(entity.getId()) && !entity.getEntityType().equals(EntityType.user)) {
                            addChildrenToModel(result, parents, model2);
                        }

                        model.add(model2);
                    }
                }
            }
            // return model;
        }

        private void treePropertyBuilder() {
            context = new EntityContextMenu(user, tree);
            context.addEntityModifiedListeners(new EntityModifiedListener());
            tree.setContextMenu(context);
            tree.setStateful(true);
            tree.setTrackMouseOver(true);
            tree.setClicksToEdit(EditorGrid.ClicksToEdit.ONE);

            //tree.getView().setAutoFill(true);
            tree.addListener(Events.RowDoubleClick, new TreeDoubleClickGridEventListener());

        }


        private class EntityModifiedListener implements EntityContextMenu.EntityModifiedListener {
            EntityModifiedListener() {
            }

            @Override
            public void onEntityDeleted(TreeModel model) {

                removeEntity(model);

            }


            @Override
            public void onEntityModifed(TreeModel model) {

                addUpdateTreeModel(model, false);

            }


            private void removeEntity(TreeModel currentModel) {

                if (tree != null && tree.getStore() != null) {


                    ModelData m = tree.getTreeStore().findModel(Parameters.id.getText(), currentModel.getBaseEntity().getId());
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

                    switch (model.getBaseEntity().getEntityType()) {
                        case user:
                            break;
                        case point:
//                            notifyEntityClickedListener(model);
//                            ReportHelper.openUrl(user, model.getUuid(), model.getName().getValue(), model.getBaseEntity().getEntityType());
                            notifyEntityClickedListener(model);
                            context.showChartPanel(model.getBaseEntity());
                            break;
                        case category:
                            notifyEntityClickedListener(model);
                            context.showChartPanel(model.getBaseEntity());
//                            ReportHelper.openUrl(user, model.getUuid(), model.getName().getValue(), model.getBaseEntity().getEntityType());
//                            notifyEntityClickedListener(model);
                            break;
                        case subscription:
                            notifyEntityClickedListener(model);
                            context.showSubscriptionPanel(model.getBaseEntity());
                            break;

                        case calculation:
                            context.showCalcPanel(model.getBaseEntity());
                            break;
                        case summary:
                            context.showSummaryPanel(model.getBaseEntity());
                            break;

                        case schedule:
                            context.showSchedulePanel(model.getBaseEntity());
                            break;
                        case webhook:
                            context.showWebHookPanel(model.getBaseEntity());
                            break;
                    }


                }
            }

        }
    }

    private class RecordValueCallback implements AsyncCallback<Void> {
        private final GridEvent be;
        private final TreeModel model;
        private final Value value;

        RecordValueCallback(Value value, GridEvent be, TreeModel model) {
            this.be = be;
            this.model = model;
            this.value = value;
        }

        @Override
        public void onFailure(final Throwable throwable) {
            be.getRecord().reject(false);
            updater.cancel();
        }

        @Override
        public void onSuccess(final Void v) {
            be.getRecord().commit(false);
            model.setDirty(false);
            updateModel(value, model);

        }
    }

    private class ReloadAsyncCallback implements AsyncCallback<Map<String, Value>> {

        ReloadAsyncCallback() {
        }

        @Override
        public void onFailure(Throwable throwable) {
            GWT.log(throwable.getMessage(), throwable);
        }

        @Override
        public void onSuccess(Map<String, Value> valueMap) {
            final TreeStore<ModelData> models = tree.getTreeStore();
            for (final ModelData m : models.getAllItems()) {
                final TreeModel model = (TreeModel) m;
                if (!model.isDirty() && model.getEntityType().equals(EntityType.point)) {

                    if (valueMap.containsKey(model.getId())) {
                        Value value = valueMap.get(model.getId());
                        //  Point p = (Point) valueMap.get(model.getId());
                        if (value == null) {
                            model.setAlertType(AlertType.OK);
                            model.setValue(new Value.Builder().doubleValue(0.0).create());

                        } else {
                            model.setAlertType(value.getAlertState());
                            model.setValue(value);
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
            TreeModel treeModel = (TreeModel) selectedModel;
            e.setCancelled(treeModel.isReadOnly());
            e.getStatus().setStatus(!treeModel.isReadOnly());

        }

        @Override
        public void dragDrop(final DNDEvent e) {
            super.dragDrop(e);
            if (!e.getTarget().getInnerHTML().equals("&nbsp;")) {
                if (selectedModel instanceof TreeModel) {

                    final TreeModel model = (TreeModel) selectedModel;
                    //final TreeModel parent = (TreeModel) model.getParent();
                    List<Entity> dropTargets = getDropTarget(e.getTarget().getInnerText());
                    if (!dropTargets.isEmpty()) {
                        selectedModel.set(Parameters.name.getText(), model.getName().getValue());
                        final Entity draggedEntity = model.getBaseEntity();
                        final Entity target = dropTargets.get(0);
                        e.setCancelled(target.isReadOnly());
                        e.getStatus().setStatus(!target.isReadOnly());
                        if (!model.isReadOnly() && !target.isReadOnly()) {
                            moveEntity(draggedEntity, target);
                        }
                    } else {
                        //fixes a bug where the dragged object vanishes - we can't seem to put it back right, we have to reload the tree
                        //  e.setCancelled(true);
                        //  e.getStatus().setStatus(false);
                        getUserEntities();
                    }


                }

            }
        }

        private List<Entity> getDropTarget(final String targetName) {

            ModelData modelData = tree.getTreeStore().findModel(Parameters.name.getText(), targetName);
            if (modelData != null) {
                List<Entity> r = new ArrayList<Entity>(1);
                r.add(((TreeModel) modelData).getBaseEntity());
                return r;

            }
            return Collections.emptyList();


        }

        private void moveEntity(final Entity draggedEntity, final Entity target) {

            if (target.getOwner().equals(draggedEntity.getOwner())) {


                draggedEntity.setParent(target.getId());

                entityService.addUpdateEntityRpc(user, draggedEntity, new MoveEntityAsyncCallback());
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


                    final Entity entity = model.getBaseEntity();
                    long timestamp = System.currentTimeMillis();


                    final String valueAndNote = model.get(Parameters.value.getText());


                    final Value value;

                    value = new Value.Builder().doubleWithData(valueAndNote).timestamp(timestamp).create();// Value.getInstance(SimpleValue.getInstance(valueAndNote), timestamp);

                    valueService.recordValueRpc(user, entity, value, new RecordValueCallback(value, be, model));


                }
            } else {
                model.setDirty(true);
            }
        }
    }

    private class GridExpandListener implements Listener<GridEvent> {

        GridExpandListener() {
        }

        @Override
        public void handleEvent(final GridEvent be) {
            updater.run();

        }
    }

    private class GridHoverListener implements Listener<GridEvent> {

        GridHoverListener() {
        }

        @Override
        public void handleEvent(final GridEvent be) {
            final TreeModel model = (TreeModel) be.getModel();


        }
    }

    private class GridBeforeEditEventListener implements Listener<GridEvent> {

        GridBeforeEditEventListener() {
        }

        @Override
        public void handleEvent(final GridEvent be) {
            final TreeModel model = (TreeModel) be.getModel();
            if (!model.getEntityType().equals(EntityType.point) || model.isReadOnly()) {
                be.setCancelled(true);
            }

        }
    }

    private class RefreshTimer extends Timer {
        RefreshTimer() {
        }

        @Override
        public void run() {

            if (tree != null) {

                reloadCurrentValues(getVisiblePoints());
            }

        }

        private void reloadCurrentValues(final Map<String, Point> entityMap) {
            valueService.getCurrentValuesRpc(user, entityMap, new ReloadAsyncCallback());
        }

        private Map<String, Point> getVisiblePoints() {
            final Map<String, Point> entityMap = new HashMap<String, Point>(tree.getTreeStore().getAllItems().size());

            if (tree != null) {
                addExpandedValueToMap(entityMap);
            }
            return entityMap;
        }

        private void addExpandedValueToMap(final Map<String, Point> entityMap) {
            for (final ModelData m : tree.getTreeStore().getAllItems()) {
                final TreeModel model = (TreeModel) m;
                try {
                    putModelInMap(entityMap, model);
                } catch (Exception ignored) { //null pointer when tree is completely collapsed.

                }
            }
        }

        private void putModelInMap(final Map<String, Point> entityMap, final TreeModel model) {
            if (model != null
                    && model.getParent() != null
                    && !model.isDirty()
                    && model.getEntityType().equals(EntityType.point)
                    ) {
                if (tree.isExpanded(model.getParent())) {
                    entityMap.put(model.getId(), (Point) model.getBaseEntity());
                }
            }
        }
    }
}