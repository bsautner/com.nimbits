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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.datapoints.PointServiceAsync;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.ui.controls.ProtectionLevelOptions;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;

import java.util.ArrayList;
import java.util.List;

public class PointPanel extends LayoutContainer {

    private ProtectionLevelOptions protectionLevelOptions;
    private final CheckBox he = new CheckBox();
    private final CheckBox idleOn = new CheckBox();
    private final CheckBox le = new CheckBox();
    private final List<PointUpdatedListener> pointUpdatedListeners = new ArrayList<PointUpdatedListener>();
    private final NumberField compression = new NumberField();
    private final NumberField expires = new NumberField();
    private final NumberField high = new NumberField();
    private final NumberField idleMinutes = new NumberField();
    private final NumberField low = new NumberField();
    private final PointServiceAsync pointService = GWT.create(PointService.class);
    private final SeparatorToolItem separatorToolItem = new SeparatorToolItem();
    private final TextArea description = new TextArea();
    private final TextField<String> unit = new TextField<String>();
    private final Entity entity;
    private Point point;
    private ComboBox<TypeOption> hysteresisType;


    public PointPanel(final Entity entity)   {
        this.entity = entity;
         protectionLevelOptions = new ProtectionLevelOptions(entity);

         loadForm();


    }
    private ComboBox<TypeOption> hysteresisTypeCombo(final FilterType selectedValue) {
        ComboBox<TypeOption> combo = new ComboBox<TypeOption>();

        ArrayList<TypeOption> ops = new ArrayList<TypeOption>();

        for (FilterType type : FilterType.values()){
            ops.add(new TypeOption(type));
        }



        ListStore<TypeOption> store = new ListStore<TypeOption>();

        store.add(ops);

        combo.setFieldLabel("Filter type");
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);
        combo.setForceSelection(true);
        TypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void loadForm()  {
        pointService.getPointByKey(entity.getKey(), new AsyncCallback<Point>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
            }

            @Override
            public void onSuccess(final Point p) {
                buildForm(p);
            }

            private void buildForm(final Point p) {
                point = p;


                setSize("475", "475");
                setLayout(new FillLayout(Orientation.VERTICAL));
                //setLayout(new FitLayout());

                com.google.gwt.user.client.ui.VerticalPanel verticalPanel = new com.google.gwt.user.client.ui.VerticalPanel();
                add(verticalPanel);
                verticalPanel.setSize("460", "450");
                verticalPanel.setBorderWidth(0);
                ToolBar mainToolBar = mainToolBar();
                verticalPanel.add(mainToolBar);

                TabPanel tabPanel = new TabPanel();
                TabItem tabAlerts = new TabItem("Alerts");

                TabItem tabGeneral = new TabItem("General");
                tabGeneral.setHeight("425");

                verticalPanel.add(tabPanel);
                tabPanel.setSize("475", "435");

                tabPanel.add(tabGeneral);

                tabPanel.add(tabAlerts);


                tabAlerts.add(alertForm());
                tabGeneral.add(generalForm());

                add(verticalPanel);
                doLayout();
            }
        });
    }

    private ToolBar mainToolBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setHeight("");
         Button buttonSave = saveButtonInit();



        separatorToolItem.setWidth("25px");

        toolBar.add(buttonSave);



        return toolBar;
    }

    private Button saveButtonInit() {
        Button buttonSave = new Button("Save");
        buttonSave.setEnabled(! entity.isReadOnly());
        buttonSave.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));


            buttonSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {

                           savePoint();

                }
            });

        return buttonSave;
    }


    private void savePoint()  {
        final MessageBox box = MessageBox.wait("Progress",
                "Saving your data, please wait...", "Saving...");
        box.show();


        //General



        entity.setDescription(description.getValue());
        entity.setProtectionLevel(protectionLevelOptions.getProtectionLevel());
        final EntityServiceAsync serviceAsync = GWT.create(EntityService.class);



        serviceAsync.addUpdateEntity(entity, new AsyncCallback<Entity>() {

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Entity entity) {


            }
        });
        point.setFilterValue(compression.getValue().doubleValue());
        point.setFilterType(hysteresisType.getValue().type);
        point.setExpire(expires.getValue().intValue());
        point.setUnit(unit.getValue());


        //Alerts

        point.setHighAlarm(high.getValue().doubleValue());
        point.setLowAlarm(low.getValue().doubleValue());
        point.setHighAlarmOn(he.getValue());
        point.setLowAlarmOn(le.getValue());

        //idlealarm
        point.setIdleAlarmOn(idleOn.getValue());
        point.setIdleSeconds(idleMinutes.getValue().intValue() * 60);
        point.setIdleAlarmSent(false);
//

       // point.setSendAlertsAsJson(sendAlertAsJson.getValue());





        pointService.updatePoint(point, new AsyncCallback<Point>() {
            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert("Alert", caught.getMessage(), null);
                box.close();
            }

            @Override
            public void onSuccess(Point result) {

                try {
                    notifyPointUpdatedListener();
                    MessageBox.alert("Success", "Point Updated", null);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }
                box.close();
            }
        });
    }

    private void notifyPointUpdatedListener() throws NimbitsException {
        for (final PointUpdatedListener pointUpdatedListener : pointUpdatedListeners) {
            pointUpdatedListener.onPointUpdated(entity);
        }
    }







    private FormPanel alertForm() {
        FormPanel simple = new FormPanel();
        Html h = new Html();


        h.setHtml("<P>Enter values that will trigger an high or low alert if the value of this point goes above or below a value. </p>" +
                "<P>Enter the number of minutes this point can go without recieving a new value before it goes into an idle alert state. </p>" +
                "<P>Right click on this point and select \"subscribe\" to configure how you'd like to be alerted to changes in this point's alert state.</p>" +
                "other users who subscribe to this point will also receive alerts based on their settings.</P><BR><BR>");


        simple.add(h);


        simple.setHeaderVisible(false);

        simple.setFrame(false);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize("450","450");
        simple.add(h);

        high.setFieldLabel("High Value");
        high.setValue(point.getHighAlarm());
        high.setAllowBlank(false);
        simple.add(high);


        he.setBoxLabel("High alert enabled");
        he.setLabelSeparator("");
        he.setValue(point.isHighAlarmOn());
        // he.setFieldLabel("Enabled");
        simple.add(he);


        low.setFieldLabel("Low Value");
        low.setAllowBlank(false);
        low.setValue(point.getLowAlarm());
        simple.add(low);


        le.setBoxLabel("Low alert enabled");
        le.setLabelSeparator("");
        le.setValue(point.isLowAlarmOn());
        simple.add(le, new FormData("0% -395"));

        idleOn.setBoxLabel("Idle alert enabled");
        idleOn.setLabelSeparator("");

        idleOn.setValue(point.isIdleAlarmOn());
        idleMinutes.setFieldLabel("Idle Minutes");
        idleMinutes.setValue(point.getIdleSeconds() / 60);


        simple.add(idleMinutes);
        simple.add(idleOn);
        if (point.isIdleAlarmOn()) {
            Html h2 = new Html();

            String s = "<P>Based on the current settings, this point is currently ";
            if (point.getIdleAlarmSent()) {
                s += "idle.";
            } else {
                s += "not idle.";
            }
            //  s += " Last recorded timestamp was: " + point.getLastRecordedTimestamp() + "</p>";
            h2.setHtml(s);

            simple.add(h2);
        }

        return simple;
    }

    //general
    private FormPanel generalForm( ) {

        FormPanel simple = new FormPanel();
        simple.setHeaderVisible(false);

        simple.setFrame(false);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize("450", "408");

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setSpacing(10);
        verticalPanel.setHeight(80);

        TableData tdVerticalPanel = new TableData();
        tdVerticalPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
        tdVerticalPanel.setMargin(5);

        compression.setFieldLabel("Compression Filter");
        hysteresisType = hysteresisTypeCombo(point.getFilterType());

        compression.setValue(point.getFilterValue());
        compression.setAllowBlank(false);
        simple.add(compression);
        simple.add(hysteresisType);



        expires.setFieldLabel("Expires (days)");
        expires.setValue(point.getExpire());
        expires.setAllowBlank(false);
        simple.add(expires);


        unit.setFieldLabel("Unit of Measure");
        unit.setValue(point.getUnit());
        unit.setAllowBlank(true);
        simple.add(unit);


        simple.add(protectionLevelOptions, new FormData("-20"));


        description.setPreventScrollbars(true);
        description.setValue(entity.getDescription());
        description.setFieldLabel("Description");
        simple.add(description, new FormData("-20"));
        description.setSize("400", "100");

        Html h = new Html("<p>Use filter types to ignore new values that are +/- the previously recorded value or above/below the floor or ceiling setting. This is useful for " +
                "filtering out noise such as small changes in a value or the same value repeated many times when you only want to record significant changes.</p>");
        simple.add(h);
        return simple;
    }


    public interface PointUpdatedListener {
        void onPointUpdated(Entity entity) throws NimbitsException;
    }

    public void addPointUpdatedListeners(PointUpdatedListener listener) {
        pointUpdatedListeners.add(listener);
    }
    private class TypeOption extends BaseModelData {
        FilterType type;


        public TypeOption(FilterType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public FilterType getMethod() {
            return type;
        }
    }
}
