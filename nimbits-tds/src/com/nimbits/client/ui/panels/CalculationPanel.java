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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
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
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.calculation.CalculationService;
import com.nimbits.client.service.calculation.CalculationServiceAsync;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class CalculationPanel extends NavigationEventProvider {

    FormData formdata;
    VerticalPanel vp;

    private Entity entity;
    private Calculation calculation;

    public CalculationPanel(Entity entity) {
        this.entity = entity;


    }
    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        // setLayout(new FillLayout());
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);

            if (entity.getEntityType().equals(EntityType.calculation)) {
                getExisting();
            }
            else {
                try {
                    createForm();
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }
                add(vp);
                doLayout();
            }

        }


    private void getExisting() {
        CalculationServiceAsync service = GWT.create(CalculationService.class);
        service.getCalculation(entity, new AsyncCallback<Calculation>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
            }

            @Override
            public void onSuccess(Calculation result) {
                calculation = result;
                try {
                    createForm();
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }
                add(vp);
                doLayout();
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


        final TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel("Calculation Name");

        if (calculation != null && entity.getEntityType().equals(EntityType.calculation)) {
            nameField.setValue(entity.getName().getValue());
        } else {
            nameField.setValue(entity.getName().getValue() + " Calc");
        }
        String xc = calculation == null ? null : calculation.getX();
        String yc =calculation == null ? null : calculation.getY();
        String zc = calculation == null ? null : calculation.getZ();
        String targetc = calculation == null ? null : calculation.getTarget();

        final EntityCombo targetcombo = new EntityCombo(EntityType.point, targetc, UserMessages.MESSAGE_SELECT_POINT);
        targetcombo.setFieldLabel("Target");

        final EntityCombo xCombo = new EntityCombo(EntityType.point, xc, UserMessages.MESSAGE_SELECT_POINT);
        xCombo.setFieldLabel("x var");

        final EntityCombo yCombo = new EntityCombo(EntityType.point, yc, UserMessages.MESSAGE_SELECT_POINT);
        yCombo.setFieldLabel("y var");

        final EntityCombo zCombo = new EntityCombo(EntityType.point, zc, UserMessages.MESSAGE_SELECT_POINT);
        zCombo.setFieldLabel("z var");



        final CheckBox enabled = new CheckBox();
        enabled.setValue(calculation != null && calculation.getEnabled());
        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        Button test = new Button("Test");
        test.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.play()));

        final TextField<String> formula = new TextField<String>();
        formula.setFieldLabel("Formula");
        formula.setValue(calculation==null? "" : calculation.getFormula());
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
                final Calculation update = createCalculation(xCombo, yCombo, zCombo, targetcombo, enabled, formula);
               runEquation(update);
            }
        });


        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                CalculationServiceAsync service = GWT.create(CalculationService.class);
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


                final Calculation update = createCalculation(xCombo, yCombo, zCombo, targetcombo, enabled, formula);

                service.addUpdateCalculation(entity, name, update, new AsyncCallback<Entity>() {
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


        Html h = new Html("<p>Whenever the current data point records a new number value, a calculation can be triggered using any of your " +
                "other data points current values as a variables. The result of the calculation can then be stored in another data point.</p>" +
                "<BR><p>Use this menu to add a formula. You can add an x, y and z variable and specify the data point used for each one. </p>" +
                "<br><p>Supported symbols are: *, +, -, *, /, ^, %, cos, sin, tan, acos, asin, atan, sqrt, sqr, log, min, max, ceil, floor, abs, neg, rndr.</p>" +
        "<br><p>Example:Adding a formula to a point named FOO x+5 with the x var drop down set to FOO and a target named BAR - if this point (FOO) receives a new " +
                "value of 10, the value of 15 will be recorded into BAR</p>");

        vp.add(h);

        final Html pn = new Html();

        if (entity.getEntityType().equals(EntityType.point)) {
            pn.setHtml("<p><b>Trigger Point Name: </b>" + entity.getName().getValue() + "</p>");

        }
        else {
            EntityServiceAsync svc = GWT.create(EntityService.class);

            svc.getEntityByUUID(calculation.getTrigger(), new AsyncCallback<Entity>() {
                @Override
                public void onFailure(Throwable throwable) {
                    GWT.log(throwable.getMessage(), throwable);
                }

                @Override
                public void onSuccess(Entity point) {
                    try {
                        pn.setHtml("<p><b>Trigger Point Name: </b>" + point.getName().getValue() + "</p>");
                    } catch (NimbitsException e) {
                       FeedbackHelper.showError(e);
                    }
                }
            });

        }






        vp.add(pn);
        simple.add(nameField, formdata);
        simple.add(formula, formdata);
        simple.add(targetcombo, formdata);
        simple.add(xCombo, formdata);
        simple.add(yCombo, formdata);
        simple.add(zCombo, formdata);
        simple.add(enabled, formdata);

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

    private Calculation createCalculation(EntityCombo xCombo, EntityCombo yCombo, EntityCombo zCombo, EntityCombo targetcombo, CheckBox enabled, TextField<String> formula) {
        final Calculation update;


        String x =xCombo.getValue() == null ? null : xCombo.getValue().getUUID();
        String y =yCombo.getValue() == null ? null : yCombo.getValue().getUUID();
        String z =zCombo.getValue() == null ? null : zCombo.getValue().getUUID();
        String target =targetcombo.getValue() == null ? null : targetcombo.getValue().getUUID();

        if (entity.getEntityType().equals(EntityType.calculation) && calculation != null) {

            update  = CalculationModelFactory.createCalculation(calculation.getTrigger(), calculation.getUUID(), enabled.getValue(), formula.getValue(), target,
                    x, y, z);

        }
        else {
            update = CalculationModelFactory.createCalculation(entity.getEntity(), null, enabled.getValue(), formula.getValue(),target,
                    x, y, z);

        }
        return update;
    }




    private static void runEquation(final Calculation calculation1)   {
        final Dialog simple = new Dialog();
        simple.setHeading("Test Result");
        simple.setButtons(Dialog.OK);
        simple.setBodyStyleName("pad-text");



        simple.setHideOnButtonClick(true);
        final CalculationServiceAsync service = GWT.create(CalculationService.class);
        service.solveEquation(calculation1, new AsyncCallback<Value>() {
            @Override
            public void onFailure(final Throwable throwable) {
                simple.addText(throwable.getMessage());
                simple.show();
            }

            @Override
            public void onSuccess(final Value result) {
                simple.addText("result: " + result.getDoubleValue());
                simple.show();
            }
        });

    }


}
