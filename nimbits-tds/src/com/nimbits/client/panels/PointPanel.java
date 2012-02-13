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
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
import com.nimbits.client.controls.PointCombo;
import com.nimbits.client.controls.ProtectionLevelOptions;
import com.nimbits.client.enums.IntelligenceResultTarget;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.CalculationFailedException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceModelFactory;
import com.nimbits.client.model.point.Calculation;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.datapoints.PointServiceAsync;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.intelligence.IntelligenceService;
import com.nimbits.client.service.intelligence.IntelligenceServiceAsync;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.settings.SettingsServiceAsync;
import com.nimbits.shared.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PointPanel extends LayoutContainer {

    private static final String XML_LABEL_STYLE = "xmlLabel";

    //  private final Button btnHelp = new Button("Help");
    private final Button btnTestCalc = new Button("Test Calc");
    private ProtectionLevelOptions protectionLevelOptions;




    private final CheckBox checkFB = new CheckBox();
    private final CheckBox checkIM = new CheckBox();
    private final CheckBox checkTwitter = new CheckBox();
    private final CheckBox alertToFacebookCheckbox = new CheckBox();
    private final CheckBox alertToEmailCheckbox = new CheckBox();
    private final CheckBox he = new CheckBox();
    private final CheckBox sendAlertAsJson = new CheckBox();

    //idle
    private final CheckBox idleOn = new CheckBox();
    private final CheckBox ignoreCompressedValues = new CheckBox();
    private final CheckBox im = new CheckBox();
    private final CheckBox le = new CheckBox();
    private final CheckBox tw = new CheckBox();
    private final Hyperlink hyperlinkRest = new Hyperlink("New hyperlink", false, "newHistoryToken");
    private final Hyperlink uuidLink = new Hyperlink("New hyperlink", false, "newHistoryToken");

    private final LabelField lblfldTips = new LabelField("<p>Whenever the <b>Trigger</b> data point records a new value, " +
            "the formula entered above will be executed. The resulting value will then be stored in the <b>Target</b> data point. " +
            "Your formula can contain a <b>lowercase</b> x, y, and z variable. The current value of the point assigned to that " +
            "variable will be used. Supported symbols are: *, +, -, *, /, ^, %, cos, sin, tan, acos, asin, atan, sqrt, sqr, log, min, " +
            "max, ceil, floor, abs, neg, rndr.</p>" +
            "<p>Example: Typically you will use the current Trigger Point as the x variable - so if your point was named mypoint and was " +
            "set as both the x and trigger value. And you had a target point call mytarget. A formula of x+1 would take whatever new " +
            "value was recorded into mypoint and save the result mypoint+1 into mytarget</p>");
    private final Label lblQrCodesThe = new Label("QR Codes: The QR Bar code below represents a link to a data screen for this point. Any device capable of reading barcodes can read this, such as a barcode app on your smart phone.");
    private final Label lblTheQrBarcode = new Label("The QR Barcode above links to this URL, which you can also use as a universal way to view this point's data:");
    private final Label lblYouCanPull = new Label("You can pull this point's data using http post and getInstance commands using the REST API Web service. This example uses this points unique UUID, but there are many ways to access this point's data. See the REST API documents to learn more. ");

    private final List<PointDeletedListener> pointDeletedListeners = new ArrayList<PointDeletedListener>();
    private final List<PointUpdatedListener> pointUpdatedListeners = new ArrayList<PointUpdatedListener>();

    //General Properties
    private final NumberField compression = new NumberField();
    private final NumberField delay = new NumberField();
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
    private PointCombo CalcXCombo;
    private PointCombo Y;
    private PointCombo Z;
    private final CheckBox calcEnabled = new CheckBox();
    //calcs
    private final PointCombo calcTarget;

//     private final Icons ICONS = GWT.create(Icons.class);


    // private Point point;

//    void setPoint(Point aPoint) {
//        this.point = aPoint;
//    }


    private final PointServiceAsync pointService = GWT.create(PointService.class);

    private final SeparatorToolItem separatorToolItem = new SeparatorToolItem();

    private final static String FORM_HEIGHT = "450";

    private final static String MAIN_WIDTH = "450";
    private final TextArea description = new TextArea();
    private final TextField<String> formula = new TextField<String>();

    private final TextField<String> trigger = new TextField<String>();
    private final TextField<String> unit = new TextField<String>();


    private final Entity entity;
    private Point point;
    private Point intelligenceTargetPoint = null;


    private final TextArea intelFormula = new TextArea();
    private final Button btnTestIntel = new Button("Test Intelligence");
    private final TextField<String> intelNodeId = new TextField<String>();
    private final TextField<String> intelTargetPoint = new TextField<String>();
    private final RadioGroup targetOption = new RadioGroup();
    private final Radio intelTargetRadioNumber = new Radio();
    private final Radio intelTargetRadioData = new Radio();


    private final CheckBox intelEnabled = new CheckBox();
    private final CheckBox intelPlainText = new CheckBox();
    private final User user;

    public PointPanel(final User user, final Entity entity)   {
        this.entity = entity;
        this.user = user;
        calcTarget= new PointCombo(user);
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
                CalcXCombo = new PointCombo(user);
                Y = new PointCombo(user);
                Z = new PointCombo(user);


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

                TabItem tabIntelligence = new TabItem("Intelligence (beta)");


                TabItem tabGeneral = new TabItem("General");
                tabGeneral.setHeight("425");
                // TabItem tabRelay = new TabItem("Relay");
                TabItem tabCalcs = new TabItem("Calculations");
                TabItem tabIdle = new TabItem("Idle Alarm");
                TabItem tabLinks = new TabItem("Links");
                //tabGeneral.setWidth("450");


                verticalPanel.add(tabPanel);
                tabPanel.setSize(FORM_HEIGHT, "435");

                tabPanel.add(tabGeneral);
                if (settingMap.containsKey(Const.SETTING_WOLFRAM) && !Utils.isEmptyString(settingMap.get(Const.SETTING_WOLFRAM))) {
                    tabPanel.add(tabIntelligence);
                }
                tabPanel.add(tabAlerts);
                tabPanel.add(tabCalcs);
                tabPanel.add(tabIdle);
                // tabPanel.add(tabRelay);
                tabPanel.add(tabLinks);


                tabIntelligence.add(intelForm());
                tabAlerts.add(alertForm(settingMap));
                tabGeneral.add(generalForm(settingMap));
                tabCalcs.add(calcForm());
                //    tabRelay.add(relayForm());
                tabLinks.add(linkForm());
                tabIdle.add(idleForm());
                tabPanel.addListener(Events.Select,
                        new Listener<BaseEvent>() {
                            public void handleEvent(BaseEvent tpe) {
                                if (point.getCalculation() != null) {
                                    if (!(Utils.isEmptyString(point.getCalculation().getTarget()))) {
                                        calcTarget.setValue(calcTarget.getStore().findModel(Const.PARAM_ID,
                                                point.getCalculation().getTarget()));
                                    }
                                    if (!(Utils.isEmptyString(point.getCalculation().getX()))) {
                                        CalcXCombo.setValue(CalcXCombo.getStore().findModel(Const.PARAM_ID, point.getCalculation().getX()));
                                        CalcXCombo.repaint();
                                    } else {
                                        GxtModel xModel = new GxtModel(user, point);
                                        CalcXCombo.setValue(xModel);
                                    }

                                    if (!(Utils.isEmptyString(point.getCalculation().getY()))) {
                                        Y.setValue(Y.getStore().findModel(Const.PARAM_ID, point.getCalculation().getY()));
                                    }

                                    if (!(Utils.isEmptyString(point.getCalculation().getZ()))) {
                                        Z.setValue(Z.getStore().findModel(Const.PARAM_ID, point.getCalculation().getZ()));
                                    }
                                }

                            }
                        });
                add(verticalPanel);
                doLayout();
            }
        });
    }

    private ToolBar mainToolBar(final Map<String, String> settingMap) {
        ToolBar toolBar = new ToolBar();
        toolBar.setHeight("");
        intelligenceTestButtonInit();

        btnTestIntel.setVisible(settingMap.containsKey(Const.SETTING_WOLFRAM) && !Utils.isEmptyString(settingMap.get(Const.SETTING_WOLFRAM)));

        Button buttonSave = saveButtonInit();


        btnTestCalc.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Play()));

        btnTestCalc.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                try {
                    runEquation(true);
                } catch (CalculationFailedException e) {
                    Window.alert(e.getMessage());
                }
            }
        });

        separatorToolItem.setWidth("25px");

        toolBar.add(buttonSave);

        toolBar.add(new SeparatorToolItem());
        toolBar.add(btnTestIntel);
        toolBar.add(btnTestCalc);
        toolBar.add(separatorToolItem);

        return toolBar;
    }

    private Button saveButtonInit() {
        Button buttonSave = new Button("Save");
        buttonSave.setEnabled(! entity.isReadOnly());
        buttonSave.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));


            buttonSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    try {
                        if (!Utils.isEmptyString(intelTargetPoint.getValue()) && !Utils.isEmptyString(intelFormula.getValue())) {
                            pointService.getPointByName(null, CommonFactoryLocator.getInstance().createName(intelTargetPoint.getValue()), new AsyncCallback<Point>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    final MessageBox box = MessageBox.alert("Target Point Not Found", "There was an error trying to find the data point name entered in the" +
                                            " Intelligence target, please enter a valid point name or nothing to not use the intelligence function", null);
                                    box.show();
                                }

                                @Override
                                public void onSuccess(Point point) {
                                    try {
                                        if (point != null) {

                                            intelligenceTargetPoint = point;

                                            savePoint();

                                        } else {
                                            final MessageBox box = MessageBox.alert("Target Point Not Found", "There was an error trying to find the data point name entered in the" +
                                                    " Intelligence target, please enter a valid point name or nothing to not use the intelligence function", null);
                                            box.show();
                                        }
                                    } catch (NimbitsException e) {
                                        GWT.log(e.getMessage(), e);
                                    }
                                }
                            });
                        } else {
                            intelligenceTargetPoint = null;
                            savePoint();
                        }


                    } catch (NimbitsException e) {
                        GWT.log(e.getMessage(), e);
                    }
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

    private void savePoint() throws NimbitsException {
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
        point.setDescription(description.getValue());
        point.setSendIM(checkIM.getValue());
        point.setSendTweet(checkTwitter.getValue());
        point.setPostToFacebook(checkFB.getValue());
        point.setTargetValue(targetValue.getValue().doubleValue());

        //Alerts
        int d = delay.getValue().intValue();
        point.setAlarmDelay(d);
        point.setHighAlarm(high.getValue().doubleValue());
        point.setLowAlarm(low.getValue().doubleValue());
        point.setHighAlarmOn(he.getValue());
        point.setLowAlarmOn(le.getValue());
        point.setAlarmToFacebook(alertToFacebookCheckbox.getValue());
        point.setAlarmToEmail(alertToEmailCheckbox.getValue());
        point.setSendAlarmIM(im.getValue());
        point.setSendAlarmTweet(tw.getValue());

        //idlealarm
        point.setIdleAlarmOn(idleOn.getValue());
        point.setIdleSeconds(idleMinutes.getValue().intValue() * 60);
        point.setIdleAlarmSent(false);
//

        point.setSendAlertsAsJson(sendAlertAsJson.getValue());


        String f = formula.getValue();
        String t = "";
        String x = "";
        String y = "";
        String z = "";
        boolean enabled = calcEnabled.getValue();


        //calcs


        t = (calcTarget.getValue() != null) ? calcTarget.getPoint().getId() : "";
        x = (CalcXCombo.getValue() != null) ? CalcXCombo.getPoint().getId() : "";
        y = (Y.getValue() != null) ? Y.getPoint().getId() : "";
        z = (Z.getValue() != null) ? Z.getPoint().getId() : "";

        if (!Utils.isEmptyString(f)) {
            Calculation calculation = PointModelFactory.createCalculation(enabled, f, t, x, y, z);
            point.setCalculation(calculation);
        }


        if (intelligenceTargetPoint != null) {
            IntelligenceResultTarget intelligenceResultTarget = intelTargetRadioData.getValue() ? IntelligenceResultTarget.data : IntelligenceResultTarget.value;

            point.setIntelligence(IntelligenceModelFactory.createIntelligenceModel(
                    this.intelEnabled.getValue(),
                    intelligenceResultTarget,
                    intelligenceTargetPoint.getId(),
                    intelFormula.getValue(),
                    intelNodeId.getValue(),
                    intelPlainText.getValue()));


        }
        //


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

    private boolean runEquation(final boolean showFeedback) throws CalculationFailedException {
        final PointModel testPoint = new PointModel();
        final RecordedValueServiceAsync dataService;
        dataService = GWT.create(RecordedValueService.class);

        if (calcTarget.getValue() == null) {
            if (showFeedback) {
                final Dialog d = new Dialog();
                d.setHeading("Error");
                d.setButtons(Dialog.OK);
                d.setBodyStyleName("pad-text");
                d.addText("You must select a target that the result of this calculation will be recorded to.");
                d.setScrollMode(Scroll.AUTO);
                d.setHideOnButtonClick(true);
                d.show();
            }
            return false;
        }

        if (calcTarget.getText().equals(point.getName().getValue())) {
            if (showFeedback) {
                final Dialog d = new Dialog();
                d.setHeading("Error");
                d.setButtons(Dialog.OK);
                d.setBodyStyleName("pad-text");
                d.addText("Infinite Loop Error");
                d.setScrollMode(Scroll.AUTO);
                d.setHideOnButtonClick(true);
                d.show();
            }
            return false;
        }


        if (formula.getValue() == null) {
            if (showFeedback) {
                final Dialog d = new Dialog();
                d.setHeading("Error");
                d.setButtons(Dialog.OK);
                d.setBodyStyleName("pad-text");
                d.addText("Please enter an equation.");
                d.setScrollMode(Scroll.AUTO);
                d.setHideOnButtonClick(true);
                d.show();
            }
            return false;
        }


        String f = formula.getValue();
        String t = "";
        String x = "";
        String y = "";
        String z = "";
        boolean enabled = false;


        //calcs


        t = (calcTarget.getValue() != null) ? calcTarget.getPoint().getId() : "";
        x = (CalcXCombo.getValue() != null) ? CalcXCombo.getPoint().getId() : "";
        y = (Y.getValue() != null) ? Y.getPoint().getId() : "";
        z = (Z.getValue() != null) ? Z.getPoint().getId() : "";


        Calculation calculation = PointModelFactory.createCalculation(enabled, f, t, x, y, z);
        testPoint.setCalculation(calculation);


        dataService.solveEquation(testPoint, new AsyncCallback<Double>() {
            @Override
            public void onFailure(Throwable caught) {
                if (showFeedback) {
                    final Dialog simple = new Dialog();
                    simple.setHeading("formula Error");
                    simple.setButtons(Dialog.OK);
                    simple.setBodyStyleName("pad-text");
                    simple.addText(caught.getMessage());
                    simple.setScrollMode(Scroll.AUTO);
                    simple.setHideOnButtonClick(true);
                    simple.show();
                }
            }

            @Override
            public void onSuccess(Double result) {
                if (showFeedback) {
                    final Dialog d = new Dialog();
                    d.setHeading("formula Success");
                    d.setButtons(Dialog.OK);
                    d.setBodyStyleName("pad-text");
                    d.addText("Result: " + result);
                    d.setScrollMode(Scroll.AUTO);
                    d.setHideOnButtonClick(true);
                    d.show();
                }
            }
        });
        return true;
    }

    private FormPanel intelForm() {
        FormPanel simple = new FormPanel();


        simple.setHeaderVisible(false);

        simple.setFrame(false);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize(MAIN_WIDTH, FORM_HEIGHT);

        Html h = new Html();


        h.setHtml("<p>When this point receives a new number or data value the input text below will be process " +
                "and the result will be stored in the target point's value or data channel. Your input can contain data from " +
                "any other point (including this one) by including it's data using this format: [pointName.value], [pointName.data]</p>" +
                "<br />");


        intelFormula.setFieldLabel("Input");
        intelNodeId.setFieldLabel("Pod ID");


        intelTargetRadioNumber.setBoxLabel("Number Value");
        intelTargetRadioData.setBoxLabel("Text Data");
        intelTargetRadioNumber.setValue(true);

        intelTargetRadioNumber.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (intelTargetRadioNumber.getValue()) {
                    intelNodeId.setValue(Const.PARAM_RESULT);
                    intelPlainText.setValue(intelTargetRadioNumber.getValue());
                }
                intelNodeId.setReadOnly(intelTargetRadioNumber.getValue());
                intelPlainText.setReadOnly(intelTargetRadioNumber.getValue());
            }
        });

        intelTargetRadioData.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (intelTargetRadioNumber.getValue()) {
                    intelNodeId.setValue(Const.PARAM_RESULT);
                    intelPlainText.setValue(intelTargetRadioNumber.getValue());

                }
                intelNodeId.setReadOnly(intelTargetRadioNumber.getValue());
                intelPlainText.setReadOnly(intelTargetRadioNumber.getValue());


            }
        });


        intelTargetPoint.setFieldLabel("Target Point");


        targetOption.setFieldLabel("Cell Result As");
        targetOption.add(intelTargetRadioNumber);
        targetOption.add(intelTargetRadioData);

        boolean enabled = (point.getIntelligence() != null) && point.getIntelligence().getEnabled();

        intelEnabled.setValue(enabled);


        intelEnabled.setBoxLabel("Enabled");
        intelEnabled.setLabelSeparator("");
        intelPlainText.setBoxLabel("<i>(Requires a Pod Id)</i>");
        intelPlainText.setFieldLabel("Results in Plain Text");
        intelPlainText.setValue(true);

        final Intelligence i = point.getIntelligence();
        if (i != null) {

            intelEnabled.setValue(i.getEnabled());
            intelPlainText.setValue(i.getResultsInPlainText());
            intelTargetRadioData.setValue(i.getResultTarget() == IntelligenceResultTarget.data);
            intelTargetRadioNumber.setValue(i.getResultTarget() == IntelligenceResultTarget.value);
            intelFormula.setValue(i.getInput());
            intelNodeId.setValue(i.getNodeId());

            pointService.getPointByID(i.getTargetPointId(), new AsyncCallback<Point>() {
                @Override
                public void onFailure(Throwable throwable) {
                    intelTargetPoint.setValue("");
                }

                @Override
                public void onSuccess(Point point) {
                    intelTargetPoint.setValue(point.getName().getValue());
                }
            });
        }


        simple.add(h);
        //    simple.add(btnTestIntel);
        simple.add(intelFormula);
        simple.add(intelNodeId);
        simple.add(targetOption);
        simple.add(intelTargetPoint);
        simple.add(intelPlainText);
        simple.add(intelEnabled);


        return simple;


    }

    private void intelligenceTestButtonInit() {
        btnTestIntel.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Play()));
        btnTestIntel.addListener(Events.OnClick, new Listener<BaseEvent>() {


            @Override
            public void handleEvent(BaseEvent be) {

                final IntelligenceServiceAsync service;
                service = GWT.create(IntelligenceService.class);
                final IntelligenceResultTarget intelligenceResultTarget;
                if (intelTargetRadioData.getValue()) {
                    intelligenceResultTarget = IntelligenceResultTarget.data;
                } else {
                    intelligenceResultTarget = IntelligenceResultTarget.value;
                }
                if (!Utils.isEmptyString(intelTargetPoint.getValue())) {


                    EntityName targetEntityName = CommonFactoryLocator.getInstance().createName(intelTargetPoint.getValue());
                    boolean getPlainText = intelPlainText.getValue();

                    service.processInput(point, intelFormula.getValue(), intelNodeId.getValue(), intelligenceResultTarget, targetEntityName, getPlainText, new AsyncCallback<String>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            final MessageBox box = MessageBox.alert("Error", throwable.getMessage(), null);
                            box.show();
                        }

                        @Override
                        public void onSuccess(String s) {
                            if (s.length() < 100) {
                                final MessageBox box = MessageBox.info("Your result", "[" + s + "]", null);
                                box.show();
                            } else if (Utils.isEmptyString(s)) {
                                final MessageBox box = MessageBox.alert("Error", "The results were empty, this may have been due to an error in the input or a timeout on the server" +
                                        ". If you see this error frequently, please report it.", null);
                                box.show();
                            } else {
                                com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();

                                s = s.replaceAll("<", "\n &#60;");
                                s = s.replaceAll(">", "&#62;");
                                Label xml = new HTML("<pre>" + s + "</pre>", false);
                                xml.setStyleName(XML_LABEL_STYLE);
                                ContentPanel contentPanel = new ContentPanel();
                                contentPanel.setFrame(true);
                                contentPanel.setScrollMode(Scroll.AUTOY);


                                final FlowPanel panel = new FlowPanel();

                                panel.add(xml);
                                contentPanel.add(panel);

                                //  Html h = new Html(s);
                                w.setHeading("XML Results");
                                w.setWidth(800);
                                w.setHeight(700);
                                w.add(contentPanel);
                                w.show();

                            }


                        }
                    });
                } else {
                    final MessageBox box = MessageBox.info("Error", "Please enter the name of an existing data point values will be saved to.", null);
                    box.show();
                }


            }
        });
    }


    private FormPanel alertForm(Map<String, String> settingMap) {
        FormPanel simple = new FormPanel();


        simple.setHeaderVisible(false);

        simple.setFrame(false);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize(MAIN_WIDTH, FORM_HEIGHT);


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

        alertToFacebookCheckbox.setVisible(settingMap.containsKey(Const.SETTING_FACEBOOK_CLIENT_ID) && !Utils.isEmptyString(settingMap.get(Const.SETTING_FACEBOOK_CLIENT_ID)));
        alertToFacebookCheckbox.setBoxLabel("Post alerts to facebook");
        alertToFacebookCheckbox.setLabelSeparator("");
        alertToFacebookCheckbox.setValue(point.getAlarmToFacebook());
        simple.add(alertToFacebookCheckbox);


        alertToEmailCheckbox.setBoxLabel("Send alerts to email");
        alertToEmailCheckbox.setLabelSeparator("");
        alertToEmailCheckbox.setValue(point.isAlarmToEmail());
        simple.add(alertToEmailCheckbox);

        im.setBoxLabel("Send alerts to IM");
        im.setLabelSeparator("");
        im.setValue(point.getSendAlarmIM());
        simple.add(im);

        tw.setVisible(settingMap.containsKey(Const.SETTING_TWITTER_SECRET) && !Utils.isEmptyString(settingMap.get(Const.SETTING_TWITTER_SECRET)));
        tw.setBoxLabel("Post alerts to twitter");
        tw.setLabelSeparator("");
        tw.setValue(point.getSendAlarmTweet());
        simple.add(tw);

        // final TextField<Integer> delay = new TextField<Integer>();


        delay.setAllowBlank(false);

        delay.setFieldLabel("Repeat email Delay (Minutes)");
        delay.setValue(point.getAlarmDelay());
        simple.add(delay);


        return simple;
    }

    //general
    private FormPanel generalForm(Map<String, String> settingMap) {
        //ContentPanel vp = new ContentPanel();

        //		vpoint.setHeaderVisible(false);
        //		vpoint.setFrame(false);
        //		vpoint.setBorders(false);
        //		vpoint.setBodyBorder(false);

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


        checkFB.setBoxLabel("facebook");
        checkFB.setValue(point.isPostToFacebook());
        checkFB.setVisible(settingMap.containsKey(Const.SETTING_FACEBOOK_CLIENT_ID) && !Utils.isEmptyString(settingMap.get(Const.SETTING_FACEBOOK_CLIENT_ID)));

        checkIM.setBoxLabel("Instant Message");
        checkIM.setValue(point.getSendIM());

        checkTwitter.setVisible(settingMap.containsKey(Const.SETTING_TWITTER_SECRET) && !Utils.isEmptyString(settingMap.get(Const.SETTING_TWITTER_SECRET)));
        checkTwitter.setBoxLabel("Twitter");
        checkTwitter.setValue(point.getSendTweet());



        CheckBoxGroup checkGroupFB = new CheckBoxGroup();
        checkGroupFB.setFieldLabel("Messaging");
        checkGroupFB.add(checkFB);
        checkFB.setWidth("108px");

        checkGroupFB.add(checkIM);
        checkIM.setWidth("132px");
        checkGroupFB.add(checkTwitter);
        checkTwitter.setWidth("82px");
        simple.add(checkGroupFB, new FormData("-115"));
        checkGroupFB.setWidth("400");

        sendAlertAsJson.setFieldLabel("M2M");
        sendAlertAsJson.setBoxLabel("Make IM (XMPP) Alerts and Messages Machine Readable");
        sendAlertAsJson.setValue(point.getSendAlertsAsJson());
        simple.add(sendAlertAsJson);


        description.setPreventScrollbars(true);
        description.setValue(point.getDescription());
        description.setFieldLabel("Description");
        simple.add(description, new FormData("-20"));
        description.setSize("400", "100");


        return simple;
    }

    private FormPanel calcForm() {
        FormPanel simple = new FormPanel();
        simple.setHeaderVisible(false);
        simple.setFrame(false);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize(MAIN_WIDTH, FORM_HEIGHT);
        trigger.setReadOnly(true);
        trigger.setValue(point.getName().getValue());
        trigger.setFieldLabel("Trigger:");
        simple.add(trigger);

        formula.setFieldLabel("Formula");

        formula.setAllowBlank(false);
        if (point.getCalculation() != null) {
            formula.setValue(point.getCalculation().getFormula());
        }

        simple.add(formula);
        calcTarget.setEditable(true);
        calcTarget.setAllowBlank(true);

        calcTarget.setFieldLabel("Target:");
        calcTarget.setForceSelection(true);
        calcTarget.setReadOnly(false);

        // target.setValue(point.getTarget()));
        // if (point.getTarget() > 0)
        // {
        // for (Point p : target.getItemSelector().)
        // }

        calcEnabled.setBoxLabel("Enabled");
        calcEnabled.setLabelSeparator("");
        calcEnabled.setValue(point.getCalculation() != null ? point.getCalculation().getEnabled() : false);

        CalcXCombo.setEditable(true);
        CalcXCombo.setFieldLabel("x");
        CalcXCombo.setAllowBlank(true);

        Y.setAllowBlank(true);
        Y.setEditable(true);
        Y.setFieldLabel("y");

        Z.setEditable(true);
        Z.setAllowBlank(true);
        Z.setFieldLabel("z");

        simple.add(calcTarget);
        simple.add(CalcXCombo);
        simple.add(Y);
        simple.add(Z);
        simple.add(calcEnabled);
        lblfldTips.setFieldLabel("Tips:");

        simple.add(lblfldTips, new FormData("100%"));

        return simple;
    }


    private VerticalPanel linkForm() {
        VerticalPanel simple = new VerticalPanel();
        String u = Window.Location.getHost();

        String infoURL = "http://" + u + "?" +
                "uuid=" + point.getUUID();


        String bcURL;

        bcURL = "http://chart.apis.google.com/chart?chs=150x150&cht=qr&chl=" +
                infoURL +
                "&chld=L|1&choe=UTF-8";

        final Image image = new Image(bcURL);

        simple.add(lblQrCodesThe);
        simple.add(image);
        image.setSize("100", "100");

        image.setUrl(bcURL);

        simple.add(lblTheQrBarcode);
        uuidLink.setHeight("44px");
        uuidLink.setHTML("UUID Link");
        uuidLink.setText(infoURL);

        simple.add(uuidLink);

        simple.add(lblYouCanPull);


        hyperlinkRest.setText("http://" + u + "/service/currentvalue?uuid=" + point.getUUID());

        simple.add(hyperlinkRest);

        return simple;
    }

    private FormPanel idleForm() {
        FormPanel simple = new FormPanel();
        simple.setHeaderVisible(false);
        simple.setFrame(false);
        simple.setBorders(false);
        simple.setBodyBorder(false);
        simple.setSize(MAIN_WIDTH, FORM_HEIGHT);

        idleOn.setBoxLabel("Alarm On");
        idleOn.setLabelSeparator("");

        idleOn.setValue(point.isIdleAlarmOn());
        idleMinutes.setFieldLabel("Idle Minutes");
        idleMinutes.setValue(point.getIdleSeconds() / 60);

        Html h = new Html();


        h.setHtml("<P>Enter the number of minutes needed to elapse for a point to be considered idle if it does not recieve a value. </P>" +
                "<BR><P>If a point does not record a value in the elapsed number of minutes, an email alert will be sent to you. </P>" +
                "<BR><P>Please note: Regardless of the idle setting, you may not get an alert for 1 to 10 minutes after the elapsed time passes, depending on system load.</P><BR><BR>");


        simple.add(h);

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
