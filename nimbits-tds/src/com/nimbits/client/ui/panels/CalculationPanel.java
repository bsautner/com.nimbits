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
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.calculation.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.helper.*;
import com.nimbits.client.ui.icons.*;


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
                createForm();
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
                createForm();
                add(vp);
                doLayout();
            }
        });
    }



    private void createForm() {

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

        final EntityCombo targetcombo = new EntityCombo(EntityType.point, targetc, Const.MESSAGE_SELECT_POINT);
        targetcombo.setFieldLabel("Target");

        final EntityCombo xCombo = new EntityCombo(EntityType.point, xc,Const.MESSAGE_SELECT_POINT);
        xCombo.setFieldLabel("x var");

        final EntityCombo yCombo = new EntityCombo(EntityType.point, yc,Const.MESSAGE_SELECT_POINT);
        yCombo.setFieldLabel("y var");

        final EntityCombo zCombo = new EntityCombo(EntityType.point, zc,Const.MESSAGE_SELECT_POINT);
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
                notifyEntityAddedListener(null);
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
                        notifyEntityAddedListener(null);
                    }

                    @Override
                    public void onSuccess(final Entity result) {
                        box.close();

                        notifyEntityAddedListener(result);
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
                    pn.setHtml("<p><b>Trigger Point Name: </b>" + point.getName().getValue() + "</p>");
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




    private void runEquation(Calculation calculation1)   {
        final Dialog simple = new Dialog();
        simple.setHeading("Test Result");
        simple.setButtons(Dialog.OK);
        simple.setBodyStyleName("pad-text");



        simple.setHideOnButtonClick(true);
        CalculationServiceAsync service = GWT.create(CalculationService.class);
        service.solveEquation(calculation1, new AsyncCallback<Value>() {
            @Override
            public void onFailure(Throwable throwable) {
                simple.addText(throwable.getMessage());
                simple.show();
            }

            @Override
            public void onSuccess(Value result) {
                simple.addText("result: " + result.getDoubleValue());
                simple.show();
            }
        });

    }


}
