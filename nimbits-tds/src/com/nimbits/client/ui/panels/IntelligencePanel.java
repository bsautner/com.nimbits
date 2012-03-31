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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.IntelligenceResultTarget;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.intelligence.IntelligenceService;
import com.nimbits.client.service.intelligence.IntelligenceServiceAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class IntelligencePanel extends NavigationEventProvider {

    FormData formdata;
    VerticalPanel vp;

    private Entity entity;
    private Intelligence intelligence;



    public IntelligencePanel(final Entity entity) {
        this.entity = entity;


    }
    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        // setLayout(new FillLayout());
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);

        if (entity.getEntityType().equals(EntityType.intelligence)) {
            getExisting();
        }
        else {
            try {
                createForm();
                add(vp);
                doLayout();
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }

    }


    private void getExisting() {
        IntelligenceServiceAsync service = GWT.create(IntelligenceService.class);
        service.getIntelligence(entity, new AsyncCallback<Intelligence>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(Intelligence result) {
                intelligence = result;
                try {
                    createForm();
                    add(vp);
                    doLayout();
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

            }
        });
    }



    private void createForm() throws NimbitsException {

        FormPanel simple = new FormPanel();
        simple.setWidth(350);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

        final TextArea intelFormula = new TextArea();

        final TextField<String> intelNodeId = new TextField<String>();
        final RadioGroup targetOption = new RadioGroup();
        final Radio intelTargetRadioNumber = new Radio();
        final Radio intelTargetRadioData = new Radio();
        final CheckBox intelEnabled = new CheckBox();
        final CheckBox intelPlainText = new CheckBox();

        final TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel("Name");

        if (intelligence != null && entity.getEntityType().equals(EntityType.intelligence)) {
            nameField.setValue(entity.getName().getValue());
        } else {
            nameField.setValue(entity.getName().getValue() + " Intelligence");
        }

        String target = intelligence == null ? null : intelligence.getTarget();

        final EntityCombo intelTargetPoint = new EntityCombo(EntityType.point, target, UserMessages.MESSAGE_SELECT_POINT);
        intelTargetPoint.setFieldLabel("Target");




        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        Button test = new Button("Test");
        test.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.play()));


        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {


            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                try {
                    notifyEntityAddedListener(null);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }
            }
        });

        test.addSelectionListener(new SelectionListener<ButtonEvent>() {



            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                IntelligenceResultTarget target = intelTargetRadioNumber.getValue() ? IntelligenceResultTarget.value
                        : IntelligenceResultTarget.data;
                final Intelligence update = createUpdate(target,
                        intelEnabled, intelTargetPoint, intelFormula, intelNodeId, intelPlainText);
                IntelligenceServiceAsync service = GWT.create(IntelligenceService.class);
                service.processInput(update, new AsyncCallback<Value>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        final MessageBox box = MessageBox.alert("Error", caught.getMessage(), null);
                        box.show();
                    }

                    @Override
                    public void onSuccess(Value result) {
                        final MessageBox box = MessageBox.alert("Result",
                                "Value: " + result.getDoubleValue() + "<br>" +
                                        "Data: " + result.getData(), null);
                        box.show();
                    }
                });

                //   final Calculation update = createCalculation(xCombo, yCombo, zCombo, targetcombo, enabled, formula);
                // runEquation(update);
            }
        });


        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                IntelligenceServiceAsync service = GWT.create(IntelligenceService.class);
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating Calculation", "please wait...");
                box.show();
                EntityName name;
                try {
                    name = CommonFactoryLocator.getInstance().createName(nameField.getValue(), EntityType.calculation);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                    return;
                }
                //    simple.add(btnTestIntel);

                IntelligenceResultTarget target = intelTargetRadioNumber.getValue() ? IntelligenceResultTarget.value
                        : IntelligenceResultTarget.data;

                final Intelligence update = createUpdate(target, intelEnabled,
                        intelTargetPoint, intelFormula, intelNodeId, intelPlainText);

                service.addUpdateIntelligence(entity, name, update, new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                        box.close();
                        MessageBox.alert("Error", e.getMessage(), null);
                        try {
                            notifyEntityAddedListener(null);
                        } catch (NimbitsException e1) {
                            FeedbackHelper.showError(e);
                        }
                    }

                    @Override
                    public void onSuccess(final Entity result) {
                        box.close();

                        try {
                            notifyEntityAddedListener(result);
                        } catch (NimbitsException e) {
                            FeedbackHelper.showError(e);
                        }
                    }
                });

            }
        });


        Html h = new Html("<p>Whenever the this point receives a new value, a query can be made " +
                "using the WolframAlpha service using your point's data as part of the query. The " +
                "result is then stored in the target point. Text and XML results can be stored in the data channel" +
                "while numeric results can be stored in the number channel. </p>");


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");



        intelFormula.setFieldLabel("Input");
        intelNodeId.setFieldLabel("Pod ID");


        intelTargetRadioNumber.setBoxLabel("Number Value");
        intelTargetRadioData.setBoxLabel("Text Data");
        intelTargetRadioNumber.setValue(true);

        intelTargetRadioNumber.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (intelTargetRadioNumber.getValue()) {
                    intelNodeId.setValue(Parameters.result.getText());
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
                    intelNodeId.setValue(Parameters.result.getText());
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

        boolean enabled = (intelligence != null) && intelligence.getEnabled();

        intelEnabled.setValue(enabled);


        intelEnabled.setBoxLabel("Enabled");
        intelEnabled.setLabelSeparator("");
        intelPlainText.setBoxLabel("<i>(Requires a Pod Id)</i>");
        intelPlainText.setFieldLabel("Results in Plain Text");
        intelPlainText.setValue(true);


        if (intelligence != null) {

            intelEnabled.setValue(intelligence.getEnabled());
            intelPlainText.setValue(intelligence.getResultsInPlainText());
            intelTargetRadioData.setValue(intelligence.getResultTarget() == IntelligenceResultTarget.data);
            intelTargetRadioNumber.setValue(intelligence.getResultTarget() == IntelligenceResultTarget.value);
            intelFormula.setValue(intelligence.getInput());
            intelNodeId.setValue(intelligence.getNodeId());

        }

        // simple.add(h);
        //    simple.add(btnTestIntel);
        simple.add(nameField, formdata);
        simple.add(intelFormula, formdata);
        simple.add(intelNodeId, formdata);
        simple.add(targetOption, formdata);
        simple.add(intelTargetPoint, formdata);
        simple.add(intelPlainText, formdata);
        simple.add(intelEnabled, formdata);


        vp.add(h);
        vp.add(pn);




        LayoutContainer c = new LayoutContainer();
        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(5));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        c.setLayout(layout);
        cancel.setWidth(100);
        submit.setWidth(100);
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        c.add(test, layoutData);
        c.add(cancel, layoutData);
        c.add(submit, layoutData);



        vp.add(simple);
        vp.add(c);
    }

    private Intelligence createUpdate(IntelligenceResultTarget target, CheckBox intelEnabled, EntityCombo intelTargetPoint, TextArea intelFormula, TextField<String> intelNodeId, CheckBox intelPlainText) {

        if (entity.getEntityType().equals(EntityType.point)) {
            return IntelligenceModelFactory.createIntelligenceModel(
                    "",
                    intelEnabled.getValue(),
                    target,
                    intelTargetPoint.getValue().getBaseEntity().getEntity(),
                    intelFormula.getValue(),
                    intelNodeId.getValue(), intelPlainText.getValue(),
                    entity.getEntity());
        }
        else {
            return IntelligenceModelFactory.createIntelligenceModel(
                    intelligence.getUUID(),
                    intelEnabled.getValue(),
                    target,
                    intelTargetPoint.getValue().getUUID(),
                    intelFormula.getValue(),
                    intelNodeId.getValue(),
                    intelPlainText.getValue(),
                    intelligence.getTrigger());
        }
    }





}
