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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.recordedvalues.*;

import java.util.*;

class PointGridPanel extends NavigationEventProvider {


    private final ListStore<GxtPointModel> store = new ListStore<GxtPointModel>();
    private final EditorGrid<GxtPointModel> grid;
    private final Map<PointName, Point> points = new HashMap<PointName, Point>();
    private final CheckBox saveToNowCheckBox = new CheckBox();
    private final CheckBox autoSaveCheckBox = new CheckBox();
    private final CheckBoxSelectionModel<GxtPointModel> sm = new CheckBoxSelectionModel<GxtPointModel>();
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
                final GxtPointModel model = (GxtPointModel) be.getModel();
                if (!model.isReadOnly()) {
                    model.setDirty(true);


                    if (be.getColIndex() == valueColumnIndex && autoSaveCheckBox.getValue()) { //only save when the value is updated
                        notify.hide();
                        final Point point = points.get(model.getName());
                        final Date timestamp = saveToNowCheckBox.getValue() ? new Date() : (Date) model.get(Const.PARAM_TIMESTAMP);
                        final Double v = model.get(Const.PARAM_VALUE);
                        final String note = model.get(Const.PARAM_NOTE);
                        final String data = model.get(Const.PARAM_DATA);
                        final Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, timestamp, model.getId(), note, data);

                        GWT.log(value.getNote());
                        GWT.log(String.valueOf(value.getNumberValue()));
                        RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
                        try {
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
                        } catch (NimbitsException e) {
                            GWT.log(e.getMessage(), e);
                        }
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
        grid = new EditorGrid<GxtPointModel>(store, new ColumnModel(gridConfig()));

        grid.setHeight("100%");
        grid.setBorders(true);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);
        notify.setStyleAttribute("color", "#FF0000");
        notify.hide();
    }

    public List<Point> getSelectedPoints() {
        final List<GxtPointModel> models = grid.getSelectionModel().getSelectedItems();
        final List<Point> retObj = new ArrayList<Point>();

        for (final GxtPointModel model : models) {
            retObj.add(points.get(model.getName()));
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
        //columnConfigs.addPointNameColumn(configs);
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

    @Override
    protected void onDetach() {

        updater.cancel();

        super.onDetach();
    }

    public void addPoint(final Point point) throws NimbitsException {
        if (!points.containsKey(point.getName())) {
            points.put(point.getName(), point);


            store.add(new GxtPointModel(point, ClientType.other));
            store.commitChanges();
        }
        updateValues();
    }


    private void updateValues() throws NimbitsException {


        store.commitChanges();
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        if (!grid.isEditing()) {


            for (final GxtPointModel model : store.getModels()) {
                if (!model.isDirty()) {
                    final Point point = points.get(model.getName());

                    dataService.getCurrentValue(point,
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


                try {
                    updateValues();
                } catch (NimbitsException ignored) {

                }
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

    public void removePoint(final Point p) {

        for (final ModelData m : store.getModels()) {

            final GxtPointModel model = (GxtPointModel) m;
            points.remove(((GxtPointModel) m).getName());
            if (model.getName().getValue().equals(p.getName().getValue())) {
                store.remove(model);
                break;
            }
        }


        //To change body of created methods use File | Settings | File Templates.
    }


    public List<Point> saveSelectedPoints() throws NimbitsException {
        final List<GxtPointModel> models = grid.getSelectionModel().getSelectedItems();
        final List<Point> retObj = new ArrayList<Point>();
        RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
        notify.hide();
        for (final GxtPointModel model : models) {
            if (model.isDirty()) {
                Date date = model.get(Const.PARAM_TIMESTAMP) == null ? new Date() : (Date) model.get(Const.PARAM_TIMESTAMP);
                final Date timestamp = saveToNowCheckBox.getValue() ? new Date() : date;
                final double v = model.get(Const.PARAM_VALUE) == null ? 0.0 : Double.valueOf(model.get(Const.PARAM_VALUE).toString());
                final String note = model.get(Const.PARAM_NOTE);
                final String data = model.get(Const.PARAM_DATA);
                final Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, timestamp, model.getId(), note, data);

                service.recordValue(points.get(model.getName()), value, new AsyncCallback<Value>() {
                    @Override
                    public void onFailure(final Throwable throwable) {

                        Info.display("Error Saving", throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(final Value value) {
                        updateModel(value, model);

                    }
                });
                model.setDirty(false);
            }
            retObj.add(points.get(model.getName()));
        }
        return retObj;

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
        notifyValueEnteredListener(points.get(model.getName()), value);
    }

    public Map<PointName, Point> getPoints() {
        return points;
    }
}
