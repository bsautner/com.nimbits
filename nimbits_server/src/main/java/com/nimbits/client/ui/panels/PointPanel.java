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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PointPanel extends BasePanel {


    private final Collection<PointUpdatedListener> pointUpdatedListeners = new ArrayList<PointUpdatedListener>(1);
    private final NumberField compression = new NumberField();
    private final NumberField expires = new NumberField();

    private final NumberField precision = new NumberField();


    private final CheckBox inferLocationCheckbox = new CheckBox();

    private final TextArea description = new TextArea();
    private final TextField<String> unit = new TextField<String>();
    private final Entity entity;


    private ComboBox<FilterTypeOption> hysteresisType;
    private ComboBox<PointTypeOption> pointType;


    public PointPanel(User user, PanelEvent listener, final Entity entity) {
        super(user, listener, "<a href=\"http://www.nimbits.com/howto_points.jsp\">Learn More: Data Points</a>");
        this.entity = entity;

        createForm();
    }


    private ComboBox<FilterTypeOption> hysteresisTypeCombo(final FilterType selectedValue) {
        final ComboBox<FilterTypeOption> combo = new ComboBox<FilterTypeOption>();

        final List<FilterTypeOption> ops = new ArrayList<FilterTypeOption>(FilterType.values().length);

        for (final FilterType type : FilterType.values()) {
            ops.add(new FilterTypeOption(type));
        }


        final ListStore<FilterTypeOption> store = new ListStore<FilterTypeOption>();

        store.add(ops);

        combo.setFieldLabel("Filter type");

        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);
        combo.setForceSelection(true);
        final FilterTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private ComboBox<PointTypeOption> pointTypeCombo(final PointType selectedValue) {
        final ComboBox<PointTypeOption> combo = new ComboBox<PointTypeOption>();

        final List<PointTypeOption> ops = new ArrayList<PointTypeOption>(PointType.values().length);

        for (final PointType type : PointType.values()) {
            ops.add(new PointTypeOption(type));
        }


        final ListStore<PointTypeOption> store = new ListStore<PointTypeOption>();

        store.add(ops);

        combo.setFieldLabel("Point Type");
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);
        combo.setForceSelection(true);
        final PointTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void createForm() {

        Point point = (Point) entity;
        compression.setFieldLabel("Compression Filter");

        hysteresisType = hysteresisTypeCombo(point.getFilterType());


        pointType = pointTypeCombo(point.getPointType());


        compression.setValue(point.getFilterValue());
        compression.setAllowBlank(false);

        simple.add(compression, formdata);
        simple.add(hysteresisType, formdata);
        simple.add(pointType, formdata);
        inferLocationCheckbox.setFieldLabel("");
        //inferLocationCheckbox.setTitle();
        inferLocationCheckbox.setBoxLabel("Infer GPS Location");
        inferLocationCheckbox.setLabelSeparator("");
        inferLocationCheckbox.setValue(point.inferLocation());

        simple.add(inferLocationCheckbox);
        expires.setFieldLabel("Expires (days)");
        expires.setValue(point.getExpire());
        expires.setAllowBlank(false);

        simple.add(expires, formdata);

        precision.setFieldLabel("Decimal Precision");
        precision.setValue(point.getPrecision());
        precision.setAllowBlank(false);

        simple.add(precision, formdata);


        unit.setFieldLabel("Unit of Measure");
        unit.setValue(point.getUnit());
        unit.setAllowBlank(true);

        simple.add(unit, formdata);


        description.setPreventScrollbars(true);
        description.setValue(entity.getDescription());
        description.setFieldLabel("Description");

        simple.add(description, formdata);
        description.setSize("400", "100");
        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {

                try {
                    savePoint();
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

            }
        });
        final Html h = new Html("<p>Use filter types to ignore new values that are +/- the previously recorded value or above/below the floor or ceiling setting. This is useful for " +
                "filtering out noise such as small changes in a value or the same value repeated many times when you only want to record significant changes.</p>");
        simple.add(h, formdata);


        completeForm();

    }


    private void savePoint() {
        final MessageBox box = MessageBox.wait("Progress",
                "Saving your data, please wait...", "Saving...");
        box.show();

        {
            //General
            final Point point = (Point) entity;


            point.setDescription(description.getValue());


            point.setFilterValue(compression.getValue().doubleValue());
            point.setFilterType(hysteresisType.getValue().type);
            point.setPointType(pointType.getValue().type);
            point.setExpire(expires.getValue().intValue());
            point.setPrecision(precision.getValue().intValue());
            point.setUnit(unit.getValue());

            point.setInferLocation(this.inferLocationCheckbox.getValue());
            //Alerts


            final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            // PointServiceAsync service = GWT.create(PointService.class);
            service.addUpdateEntityRpc(user, point, new AsyncCallback<Entity>() {
                @Override
                public void onFailure(final Throwable caught) {
                    box.close();

                    FeedbackHelper.showError(caught);


                }

                @Override
                public void onSuccess(final Entity result) {

                    try {
                        notifyPointUpdatedListener();
                        MessageBox.alert("Success", "Point Updated", null);
                    } catch (Exception e) {
                        FeedbackHelper.showError(e);
                    }
                    box.close();
                }
            });
        }
    }

    private void notifyPointUpdatedListener() {
        for (final PointUpdatedListener pointUpdatedListener : pointUpdatedListeners) {
            pointUpdatedListener.onPointUpdated(entity);
        }
    }


    public interface PointUpdatedListener {
        void onPointUpdated(Entity entity);
    }

    public void addPointUpdatedListeners(PointUpdatedListener listener) {
        pointUpdatedListeners.add(listener);
    }

    private static class FilterTypeOption extends BaseModelData {
        private static final long serialVersionUID = -4464630285165637035L;
        private FilterType type;

        public FilterTypeOption() {

        }

        public FilterTypeOption(final FilterType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public FilterType getMethod() {
            return type;
        }
    }


    private static class PointTypeOption extends BaseModelData {
        private static final long serialVersionUID = -4464630285165637035L;
        private PointType type;

        public PointTypeOption() {

        }

        public PointTypeOption(final PointType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.name());
        }

        public PointType getMethod() {
            return type;
        }
    }
}
