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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;

import java.util.*;

class PointGridPanel extends NavigationEventProvider {


    private final ListStore<GxtModel> store = new ListStore<GxtModel>();
    private final EditorGrid<GxtModel> grid;
    private final Map<String, Entity> points = new HashMap<String, Entity>();
    private final CheckBox saveToNowCheckBox = new CheckBox();
    private final CheckBox autoSaveCheckBox = new CheckBox();
    private final CheckBoxSelectionModel<GxtModel> sm = new CheckBoxSelectionModel<GxtModel>();
    private Timer updater;
    private final static int valueColumnIndex = 3;
    Label notify = new Label("You have unsaved entries! click save");


    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);

        setLayout(new FillLayout());
        getAriaSupport().setPresentation(true);

        grid.addListener(Events.AfterEdit, new Listener<GridEvent>() {

            @Override
            public void handleEvent(final GridEvent be) {
                //updater.cancel();
                final GxtModel model = (GxtModel) be.getModel();
                if (!model.isReadOnly()) {
                    model.setDirty(true);


                    if (be.getColIndex() == valueColumnIndex && autoSaveCheckBox.getValue()) { //only save when the value is updated
                        notify.hide();
                        final Entity point = points.get(model.getUUID());
                        final Date timestamp = saveToNowCheckBox.getValue() ? new Date() : (Date) model.get(Const.PARAM_TIMESTAMP);
                        final Double v = model.get(Const.PARAM_VALUE);
                        final String note = model.get(Const.PARAM_NOTE);
                        final String data = model.get(Const.PARAM_DATA);
                        final Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, timestamp, model.getId(), note, data);

                        GWT.log(value.getNote());
                        GWT.log(String.valueOf(value.getNumberValue()));
                        RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
                        //TODO
//                        service.recordValue(point, value, new AsyncCallback<Value>() {
//                            @Override
//                            public void onFailure(final Throwable throwable) {
//                                be.getRecord().reject(false);
//                                updater.cancel();
//                            }
//
//                            @Override
//                            public void onSuccess(final Value value) {
//                                be.getRecord().commit(false);
//                                model.setDirty(false);
//                                updateModel(value, model);
//
//                            }
//                        });
                    } else {
                        notify.show();
                    }


                }
                //  updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
                //  updater.run();

            }
        });

        final ContentPanel mainPanel = new ContentPanel();
        mainPanel.setTopComponent(gridToolBar());
        mainPanel.setHeaderVisible(false);
        mainPanel.setBorders(false);
        mainPanel.setBodyBorder(false);
        mainPanel.setLayout(new FitLayout());
        mainPanel.setScrollMode(Style.Scroll.AUTO);
        // grid.setHeight(400);
        mainPanel.add(grid);

        add(mainPanel);


    }

    public PointGridPanel() {
        grid = new EditorGrid<GxtModel>(store, new ColumnModel(gridConfig()));

        grid.setHeight("100%");
        grid.setBorders(true);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);
        notify.setStyleAttribute("color", "#FF0000");
        notify.hide();
    }

    public List<Entity> getSelectedPoints() {
        final List<GxtModel> models = grid.getSelectionModel().getSelectedItems();
        final List<Entity> retObj = new ArrayList<Entity>();

        for (final GxtModel model : models) {
            retObj.add(points.get(model.getId()));
        }
        return retObj;

    }

    private List<ColumnConfig> gridConfig() {
        final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        sm.setSelectionMode(Style.SelectionMode.SIMPLE);
        ColumnConfigs columnConfigs = new ColumnConfigs();
        configs.add(sm.getColumn());
        //  columnConfigs.addPropertyColumn(configs);
        configs.add(columnConfigs.alertColumn(points));
        configs.add(columnConfigs.pointNameColumn(false));
        //columnConfigs.addEntityNameColumn(configs);
        configs.add(columnConfigs.currentValueColumn());
        configs.add(columnConfigs.noteColumn());
        configs.add(columnConfigs.addDataColumn());
        configs.add(columnConfigs.timestampColumn());

        return configs;
    }


    @Override
    protected void onAttach() {

        updater = new Timer() {
            @Override
            public void run() {
              updateValues();

            }
        };
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    @Override
    protected void onDetach() {

        updater.cancel();

        super.onDetach();
    }

    public void addPoint(final Entity entity)  {
        if (!points.containsKey(entity.getEntity())) {
            points.put(entity.getEntity(), entity);
            store.add(new GxtModel(entity));
            store.commitChanges();
        }
        updateValues();
    }


    private void updateValues() {


        store.commitChanges();
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        if (!grid.isEditing()) {


            for (final GxtModel model : store.getModels()) {
                if (!model.isDirty()) {
                    final Entity entity = points.get(model.getId());
                      //TODO
                    dataService.getCurrentValue(entity,
                            new AsyncCallback<Value>() {

                                @Override
                                public void onFailure(final Throwable caught) {

                                    notify.setText("There was a problem communicating with the server, you may need to refresh your browser");
                                    notify.show();
                                    updater.cancel();
                                }


                                @Override
                                public void onSuccess(final Value result) {


                                    if (!(result == null)) {
                                        Date current = model.get(Const.PARAM_TIMESTAMP) == null ? new Date(0) : (Date) model.get(Const.PARAM_TIMESTAMP);
                                        //protects against possible race condition on updates
                                        if (model.get(Const.PARAM_TIMESTAMP) == null || (result.getTimestamp().getTime() > current.getTime())) {
                                            updateModel(result, model);
                                        }
                                    }

                                }

                            });

                }

            }

        } else {
            notify.hide();
        }

    }


    private ToolBar gridToolBar() {
        final ToolBar t = new ToolBar();

        final Button refresh = new Button();
        refresh.setText("");

        refresh.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh2()));
        refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                     updateValues();
             }

        });


        saveToNowCheckBox.setBoxLabel("Save with Current Time");
        saveToNowCheckBox.setValue(true);

        autoSaveCheckBox.setBoxLabel("Auto-Save on new number value entry");
        autoSaveCheckBox.setValue(true);


// t.add(save);
        t.add(refresh);

        // t.add(pause);
        // t.add(play);

        t.add(new SeparatorToolItem());

        t.add(saveToNowCheckBox);
        t.add(autoSaveCheckBox);
        t.add(new SeparatorToolItem());
        t.add(notify);

        return t;

    }

    public void removePoint(final Entity p) {

        for (final ModelData m : store.getModels()) {

            final GxtModel model = (GxtModel) m;
            points.remove(((GxtModel) m).getName());
            if (model.getName().getValue().equals(p.getName().getValue())) {
                store.remove(model);
                break;
            }
        }


        //To change body of created methods use File | Settings | File Templates.
    }


    public List<Entity> saveSelectedPoints() throws NimbitsException {
        final List<GxtModel> models = grid.getSelectionModel().getSelectedItems();
        final List<Entity> retObj = new ArrayList<Entity>();
        RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
        notify.hide();
        for (final GxtModel model : models) {
            if (model.isDirty()) {
                Date date = model.get(Const.PARAM_TIMESTAMP) == null ? new Date() : (Date) model.get(Const.PARAM_TIMESTAMP);
                final Date timestamp = saveToNowCheckBox.getValue() ? new Date() : date;
                final double v = model.get(Const.PARAM_VALUE) == null ? 0.0 : Double.valueOf(model.get(Const.PARAM_VALUE).toString());
                final String note = model.get(Const.PARAM_NOTE);
                final String data = model.get(Const.PARAM_DATA);
                final Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, timestamp, model.getId(), note, data);
                 //TODO
//                service.recordValue(points.get(model.getName()), value, new AsyncCallback<Value>() {
//                    @Override
//                    public void onFailure(final Throwable throwable) {
//
//                        Info.display("Error Saving", throwable.getMessage());
//                    }
//
//                    @Override
//                    public void onSuccess(final Value value) {
//                        updateModel(value, model);
//
//                    }
//                });
                model.setDirty(false);
            }
            retObj.add(points.get(model.getUUID()));
        }
        return retObj;

    }

    private void updateModel(Value value, GxtModel model) {
        model.set(Const.PARAM_VALUE, value.getNumberValue());
        model.set(Const.PARAM_DATA, value.getData());
        model.set(Const.PARAM_TIMESTAMP, value.getTimestamp());
        model.set(Const.PARAM_NOTE, value.getNote());

        model.setAlertType(value.getAlertState());

//be.getRecord().commit(false);
        model.setDirty(false);
        store.update(model);
        notifyValueEnteredListener(points.get(model.getUUID()), value);
    }

    public Map<String, Entity> getPoints() {
        return points;
    }
}
