/*
 * Copyright (c) 2010 Nimbits Inc.
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
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
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

    private static final int WIDTH = 350;

    private FormData formdata;
    private VerticalPanel vp;
    private Entity entity;
   // private Calculation calculation;
    private User user;

    public CalculationPanel(final User user, final Entity entity) {
        this.entity = entity;
        this.user = user;


    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        setLayout(new FillLayout());
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);


            try {
                createForm();
                add(vp);
                doLayout();
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }



    }

    private void createForm() throws NimbitsException {

        final FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);
        final TextField<String> formula = new TextField<String>();
        final CheckBox enabled = new CheckBox();
        final TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel("Calculation Name");
        final Html pn = new Html();

        String xc = null;
        String yc = null;
        String zc = null;
        String targetKey = null;
        if (entity.getEntityType().equals(EntityType.calculation)) {
            nameField.setValue(entity.getName().getValue());
            xc = ((Calculation)entity).getX();
            yc =((Calculation)entity).getY();
            zc = ((Calculation)entity).getZ();
            targetKey = ((Trigger) entity).getTarget();
            enabled.setValue(((Trigger) entity).isEnabled());
            formula.setValue(((Calculation)entity).getFormula());
            pn.setHtml("<p><b>Trigger Point Name: </b>" + ((Trigger) entity).getTrigger() + "</p>");

        } else {
            nameField.setValue(entity.getName().getValue() + " Calc");
            enabled.setValue(false);
            pn.setHtml("<p><b>Trigger Point Name: </b>" + entity.getName().getValue() + "</p>");
        }



        final EntityCombo targetCombo = new EntityCombo(user, EntityType.point, targetKey, UserMessages.MESSAGE_SELECT_POINT);
        targetCombo.setFieldLabel("Target");

        final EntityCombo xCombo = new EntityCombo(user, EntityType.point, xc, UserMessages.MESSAGE_SELECT_POINT);
        xCombo.setFieldLabel("x var");

        final EntityCombo yCombo = new EntityCombo(user, EntityType.point, yc, UserMessages.MESSAGE_SELECT_POINT);
        yCombo.setFieldLabel("y var");

        final EntityCombo zCombo = new EntityCombo(user, EntityType.point, zc, UserMessages.MESSAGE_SELECT_POINT);
        zCombo.setFieldLabel("z var");

        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        final Button submit = new Button("Submit");
        final Button cancel = new Button("Cancel");
        final Button test = new Button("Test");
        test.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.play()));


        formula.setFieldLabel("Formula");

        cancel.addSelectionListener(new CancelButtonEventSelectionListener());

        test.addSelectionListener(new TestButtonEventSelectionListener(nameField, xCombo, yCombo, zCombo, targetCombo, enabled, formula));


        submit.addSelectionListener(new SubmitButtonEventSelectionListener(nameField, xCombo, yCombo, zCombo, targetCombo, enabled, formula));


        final Html h = new Html("<p>Whenever the current data point records a new number value, a calculation can be triggered using any of your " +
                "other data points current values as a variables. The result of the calculation can then be stored in another data point.</p>" +
                "<BR><p>Use this menu to add a formula. You can add an x, y and z variable and specify the data point used for each one. </p>" +
                "<br><p>Supported symbols are: *, +, -, *, /, ^, %, cos, sin, tan, acos, asin, atan, sqrt, sqr, log, min, max, ceil, floor, abs, neg, rndr.</p>" +
                "<br><p>Example:Adding a formula to a point named FOO x+5 with the x var drop down set to FOO and a target named BAR - if this point (FOO) receives a new " +
                "value of 10, the value of 15 will be recorded into BAR</p>");

        vp.add(h);









        vp.add(pn);
        simple.add(nameField, formdata);
        simple.add(formula, formdata);
        simple.add(targetCombo, formdata);
        simple.add(xCombo, formdata);
        simple.add(yCombo, formdata);
        simple.add(zCombo, formdata);
        simple.add(enabled, formdata);

        final LayoutContainer c = new LayoutContainer();
        final HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(5));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        c.setLayout(layout);
        cancel.setWidth(100);
        submit.setWidth(100);
        final HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        c.add(test, layoutData);
        c.add(cancel, layoutData);
        c.add(submit, layoutData);



        vp.add(simple);
        vp.add(c);
    }

    private Calculation createCalculation(final EntityName name, final EntityCombo xCombo, final EntityCombo yCombo, final EntityCombo zCombo, final EntityCombo targetcombo, final CheckBox enabled, final TextField<String> formula) throws NimbitsException {
        final Calculation c;


        final String x =xCombo.getValue() == null ? null : xCombo.getValue().getKey();
        final String y =yCombo.getValue() == null ? null : yCombo.getValue().getKey();
        final String z =zCombo.getValue() == null ? null : zCombo.getValue().getKey();
        final String target =targetcombo.getValue() == null ? null : targetcombo.getValue().getKey();

        if (entity.getEntityType().equals(EntityType.calculation)) {
            c = (Calculation)entity;
            c.setEnabled(enabled.getValue());
            c.setFormula(formula.getValue());
            c.setX(x);

            c.setY(y);
            c.setZ(z);
            c.setTarget(target);


        }
        else {

            Entity e = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe
                    , entity.getKey(), entity.getOwner());
            c = CalculationModelFactory.createCalculation(e, entity.getKey(),  enabled.getValue(), formula.getValue(),target,
                    x, y, z);

        }
        return c;
    }


//    private static class GetEntityAsyncCallback implements AsyncCallback<List<Entity>> {
//        private final Html pn;
//
//        GetEntityAsyncCallback(Html pn) {
//            this.pn = pn;
//        }
//
//        @Override
//        public void onFailure(final Throwable throwable) {
//            GWT.log(throwable.getMessage(), throwable);
//        }
//
//        @Override
//        public void onSuccess(final List<Entity> point) {
//            try {
//                pn.setHtml("<p><b>Trigger Point Name: </b>" + point.get(0).getName().getValue() + "</p>");
//            } catch (NimbitsException e) {
//                FeedbackHelper.showError(e);
//            }
//        }
//    }

//    private class GetExistingCalcAsyncCallback implements AsyncCallback<List<Entity>> {
//        GetExistingCalcAsyncCallback() {
//        }
//
//        @Override
//        public void onFailure(final Throwable caught) {
//            FeedbackHelper.showError(caught);
//        }
//
//        @Override
//        public void onSuccess(final List<Entity> result) {
//            if (! result.isEmpty()) {
//                calculation = (Calculation) result.get(0);
//                try {
//                    createForm();
//                    add(vp);
//                } catch (NimbitsException e) {
//                    FeedbackHelper.showError(e);
//                }
//            }
//        }
//    }

    private class CancelButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CancelButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(final ButtonEvent buttonEvent) {
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
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
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }
        private void runEquation(final Calculation calculation1)   {
            final Dialog simple = new Dialog();
            simple.setHeading("Test Result");
            simple.setButtons(Dialog.OK);
            simple.setBodyStyleName("pad-text");



            simple.setHideOnButtonClick(true);
            final CalculationServiceAsync service = GWT.create(CalculationService.class);
            service.solveEquation(user, calculation1, new GetValueAsyncCallback(simple));

        }

        private class GetValueAsyncCallback implements AsyncCallback<Value> {
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
            public void onSuccess(final Value result) {
                simple.addText("result: " + result.getDoubleValue());
                simple.show();
            }
        }
    }

    private class AddCalcEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        AddCalcEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(final Throwable e) {
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
            final EntityServiceAsync service = GWT.create(EntityService.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Calculation", "please wait...");
            box.show();
            final Calculation update;
            try {
                final EntityName name = CommonFactory.createName(nameField.getValue(), EntityType.calculation);
                update = createCalculation(name, xCombo, yCombo, zCombo, targetcombo, enabled, formula);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
                return;
            }

            service.addUpdateEntity(update, new AddCalcEntityAsyncCallback(box));

        }
    }
}
