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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.service.value.ValueServiceRpc;
import com.nimbits.client.service.value.ValueServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.client.ui.icons.Icons;

import java.util.List;


public class CalculationPanel extends BasePanel {
    private final Entity entity;


    public CalculationPanel(PanelEvent listener, final Entity entity) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_calcs.jsp\">Learn More: Calculations Help</a>");
        this.entity = entity;
        createForm();
    }


    private void createForm() {


        final TextField<String> formula = new TextField<String>();
        final CheckBox enabled = new CheckBox();
        final TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel("Name");
        final Html pn = new Html();

        String xc = null;
        String yc = null;
        String zc = null;
        String targetKey = null;
        if (entity.getEntityType().equals(EntityType.calculation)) {
            nameField.setValue(entity.getName().getValue());
            xc = ((Calculation) entity).getX();
            yc = ((Calculation) entity).getY();
            zc = ((Calculation) entity).getZ();
            targetKey = ((Trigger) entity).getTarget();
            enabled.setValue(((Trigger) entity).isEnabled());
            formula.setValue(((Calculation) entity).getFormula());
            pn.setHtml("<p><b>Trigger Point: </b>" + ((Trigger) entity).getTrigger() + "</p>");

        } else {
            nameField.setValue(entity.getName().getValue() + " calculation trigger");
            enabled.setValue(false);
            pn.setHtml("<p><b>Trigger Point: </b>" + entity.getName().getValue() + "</p>");
        }


        final EntityCombo targetCombo = new EntityCombo(EntityType.point, targetKey,  MESSAGE_SELECT_POINT);
        targetCombo.setFieldLabel("Target");

        final EntityCombo xCombo = new EntityCombo(EntityType.point, xc, MESSAGE_SELECT_POINT);
        xCombo.setFieldLabel("x var");

        final EntityCombo yCombo = new EntityCombo(EntityType.point, yc,  MESSAGE_SELECT_POINT);
        yCombo.setFieldLabel("y var");

        final EntityCombo zCombo = new EntityCombo(EntityType.point, zc,  MESSAGE_SELECT_POINT);
        zCombo.setFieldLabel("z var");

        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");


        final Button test = new Button("Test");
        test.setIcon((Icons.INSTANCE.play()));


        formula.setFieldLabel("Formula");


        test.addSelectionListener(new TestButtonEventSelectionListener(nameField, xCombo, yCombo, zCombo, targetCombo, enabled, formula));


        submit.addSelectionListener(new SubmitButtonEventSelectionListener(nameField, xCombo, yCombo, zCombo, targetCombo, enabled, formula));

        enabled.setValue(true);

        vp.add(pn);
        simple.add(nameField, formdata);
        simple.add(formula, formdata);
        simple.add(targetCombo, formdata);
        simple.add(xCombo, formdata);
        simple.add(yCombo, formdata);
        simple.add(zCombo, formdata);
        simple.add(enabled, formdata);

        super.completeForm();

    }

    private Calculation createCalculation(final EntityName name, final EntityCombo xCombo, final EntityCombo yCombo, final EntityCombo zCombo, final EntityCombo targetcombo, final CheckBox enabled, final TextField<String> formula) {


        final String x = xCombo.getValue() == null ? null : xCombo.getValue().getKey();
        final String y = yCombo.getValue() == null ? null : yCombo.getValue().getKey();
        final String z = zCombo.getValue() == null ? null : zCombo.getValue().getKey();
        final String target = targetcombo.getValue() == null ? null : targetcombo.getValue().getKey();

        CalculationModel.Builder builder = new CalculationModel.Builder();
        if (entity.getEntityType().equals(EntityType.calculation)) {
            builder.init((Calculation) entity);




        }
        Calculation update = builder.name(name)
                .owner(entity.getOwner())
                .trigger(entity.getKey())
                .enabled(enabled.getValue())
                .formula(formula.getValue())
                .target(target)
                .x(x)
                .y(y)
                .z(z)
                .create();

        return update;
    }


    private class TestButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        private final TextField<String> nameField;
        private final EntityCombo xCombo;
        private final EntityCombo yCombo;
        private final EntityCombo zCombo;
        private final EntityCombo targetcombo;
        private final CheckBox enabled;
        private final TextField<String> formula;

        TestButtonEventSelectionListener(TextField<String> nameField, EntityCombo xCombo, EntityCombo yCombo, EntityCombo zCombo, EntityCombo targetcombo, CheckBox enabled, TextField<String> formula) {
            this.nameField = nameField;
            this.xCombo = xCombo;
            this.yCombo = yCombo;
            this.zCombo = zCombo;
            this.targetcombo = targetcombo;
            this.enabled = enabled;
            this.formula = formula;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            try {
                EntityName name = CommonFactory.createName(nameField.getValue(), EntityType.calculation);
                final Calculation update = createCalculation(name, xCombo, yCombo, zCombo, targetcombo, enabled, formula);
                runEquation(update);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }

        }

        private void runEquation(final Calculation calculation1) {
            final Dialog simple = new Dialog();
            // simple.seth("Test Result");
            simple.setButtons(Dialog.OK);
            simple.setBodyStyleName("pad-text");


            simple.setHideOnButtonClick(true);
            final ValueServiceRpcAsync service = GWT.create(ValueServiceRpc.class);
            service.solveEquationRpc(calculation1, new GetValueAsyncCallback(simple));

        }

        private class GetValueAsyncCallback implements AsyncCallback<List<Value>> {
            private final Dialog simple;

            GetValueAsyncCallback(Dialog simple) {
                this.simple = simple;
            }

            @Override
            public void onFailure(final Throwable throwable) {
                simple.addText(throwable.getMessage());
                simple.show();
            }

            @Override
            public void onSuccess(final List<Value> result) {
                if (!result.isEmpty()) {
                    simple.addText("result: " + result.get(0).getDoubleValue());
                    simple.show();
                } else {
                    simple.addText("no result!");
                    simple.show();
                }
            }
        }
    }


    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final TextField<String> nameField;
        private final EntityCombo xCombo;
        private final EntityCombo yCombo;
        private final EntityCombo zCombo;
        private final EntityCombo targetcombo;
        private final CheckBox enabled;
        private final TextField<String> formula;

        SubmitButtonEventSelectionListener(TextField<String> nameField, EntityCombo xCombo, EntityCombo yCombo, EntityCombo zCombo, EntityCombo targetcombo, CheckBox enabled, TextField<String> formula) {
            this.nameField = nameField;
            this.xCombo = xCombo;
            this.yCombo = yCombo;
            this.zCombo = zCombo;
            this.targetcombo = targetcombo;
            this.enabled = enabled;
            this.formula = formula;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Calculation", "please wait...");
            box.show();
            final Calculation update;
            try {
                final EntityName name = CommonFactory.createName(nameField.getValue(), EntityType.calculation);
                update = createCalculation(name, xCombo, yCombo, zCombo, targetcombo, enabled, formula);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
                return;
            }

            service.addUpdateEntityRpc(update, new AddEntityAsyncCallback(box));

        }
    }
}
