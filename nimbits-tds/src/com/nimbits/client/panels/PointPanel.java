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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
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
import com.google.gwt.user.client.ui.Image;
import com.nimbits.client.controls.ProtectionLevelOptions;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.datapoints.PointServiceAsync;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.settings.SettingsServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointPanel extends LayoutContainer {

    private static final String XML_LABEL_STYLE = "xmlLabel";

    //  private final Button btnHelp = new Button("Help");

    private ProtectionLevelOptions protectionLevelOptions;
    private final CheckBox he = new CheckBox();
    //idle
    private final CheckBox idleOn = new CheckBox();
    private final CheckBox ignoreCompressedValues = new CheckBox();
   // private final CheckBox im = new CheckBox();
    private final CheckBox le = new CheckBox();

    private final List<PointDeletedListener> pointDeletedListeners = new ArrayList<PointDeletedListener>();
    private final List<PointUpdatedListener> pointUpdatedListeners = new ArrayList<PointUpdatedListener>();

    //General Properties
    private final NumberField compression = new NumberField();

    private final NumberField expires = new NumberField();

    //Alerts
    private final NumberField high = new NumberField();
    private final NumberField idleMinutes = new NumberField();
    //    private final NumberField lat = new NumberField();
//    private final NumberField lng = new NumberField();
    private final NumberField low = new NumberField();
    // private final TextField<String> High = new TextField<String>();
    ///private final TextField<String> Low = new TextField<String>();
    private final NumberField targetValue = new NumberField();




    private final PointServiceAsync pointService = GWT.create(PointService.class);

    private final SeparatorToolItem separatorToolItem = new SeparatorToolItem();

    private final static String FORM_HEIGHT = "450";

    private final static String MAIN_WIDTH = "450";
    private final TextArea description = new TextArea();
    private final TextField<String> formula = new TextField<String>();

    private final TextField<String> unit = new TextField<String>();


    private final Entity entity;
    private Point point;


  //  private final User user;

    public PointPanel(final Entity entity)   {
        this.entity = entity;
       // this.user = user;

        protectionLevelOptions = new ProtectionLevelOptions(entity);

        SettingsServiceAsync settings = GWT.create(SettingsService.class);
        settings.getSettings(new AsyncCallback<Map<String, String>>() {
            @Override
            public void onFailure(Throwable e) {
                GWT.log(e.getMessage(), e);
            }

            @Override
            public void onSuccess(Map<String, String> settingMap) {
                try {
                    loadForm(settingMap);
                } catch (NimbitsException e) {
                    GWT.log(e.getMessage());
                }
            }
        });

    }

    private void loadForm(final Map<String, String> settingMap) throws NimbitsException {
        pointService.getPointByUUID(entity.getEntity(), new AsyncCallback<Point>() {
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


                setSize(FORM_HEIGHT, "475");
                setLayout(new FillLayout(Orientation.VERTICAL));
                //setLayout(new FitLayout());

                com.google.gwt.user.client.ui.VerticalPanel verticalPanel = new com.google.gwt.user.client.ui.VerticalPanel();
                add(verticalPanel);
                verticalPanel.setSize("460", "450");
                verticalPanel.setBorderWidth(0);
                ToolBar mainToolBar = mainToolBar(settingMap);
                verticalPanel.add(mainToolBar);

                TabPanel tabPanel = new TabPanel();
                TabItem tabAlerts = new TabItem("Alerts");

                TabItem tabGeneral = new TabItem("General");
                tabGeneral.setHeight("425");

                verticalPanel.add(tabPanel);
                tabPanel.setSize(FORM_HEIGHT, "435");

                tabPanel.add(tabGeneral);

                tabPanel.add(tabAlerts);


                tabAlerts.add(alertForm(settingMap));
                tabGeneral.add(generalForm(settingMap));

                add(verticalPanel);
                doLayout();
            }
        });
    }

    private ToolBar mainToolBar(final Map<String, String> settingMap) {
        ToolBar toolBar = new ToolBar();
        toolBar.setHeight("");
         Button buttonSave = saveButtonInit();



        separatorToolItem.setWidth("25px");

        toolBar.add(buttonSave);

        toolBar.add(new SeparatorToolItem());


        toolBar.add(separatorToolItem);

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

    private void notifyPointDeletedListener(Point p) {
        for (PointDeletedListener pointDeletedListener : pointDeletedListeners) {
            pointDeletedListener.onPointDeleted(p);
        }
    }

//    private Point point() {
//
//        return point;
//
//    }

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
        point.setCompression(compression.getValue().doubleValue());
        point.setIgnoreIncomingCompressedValues(ignoreCompressedValues.getValue());
        point.setExpire(expires.getValue().intValue());
        point.setUnit(unit.getValue());

        point.setTargetValue(targetValue.getValue().doubleValue());

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
                MessageBox.alert("Success", "Point Updated", null);
                notifyPointUpdatedListener(entity);
                box.close();
            }
        });
    }

    private void notifyPointUpdatedListener(Entity p) {
        for (final PointUpdatedListener pointUpdatedListener : pointUpdatedListeners) {
            pointUpdatedListener.onPointUpdated(entity);
        }
    }







    private FormPanel alertForm(Map<String, String> settingMap) {
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
        simple.setSize(MAIN_WIDTH, FORM_HEIGHT);
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
    private FormPanel generalForm(Map<String, String> settingMap) {

        FormPanel simple = new FormPanel();
        simple.setHeaderVisible(false);

        simple.setFrame(false);
        //simple.setWidth(600);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize("450", "408");
        Image i = new Image();
        // i.setStyleName("#images-view .thumb img");
        i.setUrl("http://chart.apis.google.com/chart?chs=100x100&cht=qr&chl=" + "http://" + point.getHost() + "?uuid="
                + point.getUUID() + "&chld=L|1&choe=UTF-8");
        //i.setSize("75px", "75px");


        HorizontalPanel titlebar = new HorizontalPanel();
//        String host;
//
//        if (point.getHost().contains("127.0.0.1")) {
//            host = point.getHost() + ":8888";
//        } else {
//            host = point.getHost();
//        }
        String host = GWT.getModuleBaseURL();

        String ht = "<A href = \"http://" + host + "?uuid=" + point.getUUID() + "\" target=\"_blank\"> UUID:" + point.getUUID() + "</A>";


        titlebar.add(i);

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setSpacing(10);
        verticalPanel.setHeight(80);


        Html h = new Html(ht);
        verticalPanel.add(h);
        TableData tdVerticalPanel = new TableData();
        tdVerticalPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
        tdVerticalPanel.setMargin(5);
        titlebar.add(verticalPanel, tdVerticalPanel);


        //	vpoint.add(titlebar);
        //	titlebar.setSize("450", "100");


        compression.setFieldLabel("Compression");
        compression.setValue(point.getCompression());
        compression.setAllowBlank(false);
        simple.add(compression);

        ignoreCompressedValues.setFieldLabel("Apply compression to incoming data");
        ignoreCompressedValues.setValue(point.getIgnoreIncomingCompressedValues());
        //simple.add(ignoreCompressedValues);


        targetValue.setFieldLabel("Target");
        targetValue.setValue(point.getTargetValue());
        targetValue.setAllowBlank(false);
        simple.add(targetValue);


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


        return simple;
    }





    public interface PointDeletedListener {
        void onPointDeleted(Point p);
    }

    public void addPointDeletedListeners(PointDeletedListener listener) {
        pointDeletedListeners.add(listener);
    }

    public interface PointUpdatedListener {
        void onPointUpdated(Entity entity);
    }

    public void addPointUpdatedListeners(PointUpdatedListener listener) {
        pointUpdatedListeners.add(listener);
    }
}
