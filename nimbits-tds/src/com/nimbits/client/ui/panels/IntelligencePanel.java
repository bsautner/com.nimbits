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
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import static com.google.gwt.user.client.Window.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.trigger.*;
import com.nimbits.client.model.value.*;
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
public class IntelligencePanel extends NavigationEventProvider {

    private static final int WIDTH = 350;
    private FormData formdata;
    private VerticalPanel vp;
    private final Entity entity;




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

        final TextArea intelFormula = new TextArea();

        final TextField<String> intelNodeId = new TextField<String>();


        final CheckBox intelEnabled = new CheckBox();
        final CheckBox intelPlainText = new CheckBox();

        final TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel("Name");
        String target = null;

        if (entity.getEntityType().equals(EntityType.intelligence)) {
            Trigger intelligence = (Trigger) entity;
            nameField.setValue(entity.getName().getValue());
            target =  intelligence.getTarget();
        }
        else {
            nameField.setValue(entity.getName().getValue() + " Intelligence");
        }



        final EntityCombo intelTargetPoint = new EntityCombo(EntityType.point, target, UserMessages.MESSAGE_SELECT_POINT);
        intelTargetPoint.setFieldLabel("Target");




        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        Button test = new Button("Test");
        test.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.play()));


        cancel.addSelectionListener(new CancelButtonEventSelectionListener());

        test.addSelectionListener(new SelectionListener<ButtonEvent>() {



            @Override
            public void componentSelected(ButtonEvent buttonEvent) {

                alert("Intelligence testing is temporarily unavailable");
//                Intelligence update = createUpdate();
//
//                IntelligenceServiceAsync service = GWT.create(IntelligenceService.class);
//                service.processInput(update, new TestInputValueAsyncCallback());


            }
        });


        submit.addSelectionListener(new SubmitButtonEventSelectionListener(nameField, intelTargetPoint, intelEnabled, intelFormula, intelNodeId, intelPlainText));


        Html h = new Html("<p>Whenever the this point receives a new value, a query can be made " +
                "using the WolframAlpha service using your point's data as part of the query. The " +
                "result is then stored in the target point. Text and XML results can be stored in the data channel" +
                "while numeric results can be stored in the number channel. </p>");


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");


        intelFormula.setFieldLabel("Input");
        intelNodeId.setFieldLabel("Pod ID");



        intelTargetPoint.setFieldLabel("Target Point");

        intelEnabled.setBoxLabel("Enabled");
        intelEnabled.setLabelSeparator("");
        intelPlainText.setBoxLabel("<i>(Requires a Pod Id)</i>");
        intelPlainText.setFieldLabel("Results in Plain Text");
        intelPlainText.setValue(true);


        if (entity.getEntityType().equals(EntityType.calculation)) {


            Intelligence intelligence = (Intelligence)entity;

            intelEnabled.setValue(intelligence.isEnabled());
            intelPlainText.setValue(intelligence.getResultsInPlainText());
            intelFormula.setValue(intelligence.getInput());
            intelNodeId.setValue(intelligence.getNodeId());

        }
        else {
            intelEnabled.setValue(false);
        }

        // simple.add(h);
        //    simple.add(btnTestIntel);
        simple.add(nameField, formdata);
        simple.add(intelFormula, formdata);
        simple.add(intelNodeId, formdata);
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


    private static class TagetBaseEventListener implements Listener<BaseEvent> {
        private final Radio intelTargetRadioNumber;
        private final TextField<String> intelNodeId;
        private final CheckBox intelPlainText;

        TagetBaseEventListener(Radio intelTargetRadioNumber, TextField<String> intelNodeId, CheckBox intelPlainText) {
            this.intelTargetRadioNumber = intelTargetRadioNumber;
            this.intelNodeId = intelNodeId;
            this.intelPlainText = intelPlainText;
        }

        @Override
        public void handleEvent(BaseEvent be) {
            if (intelTargetRadioNumber.getValue()) {
                intelNodeId.setValue(Parameters.result.getText());
                intelPlainText.setValue(intelTargetRadioNumber.getValue());

            }
            intelNodeId.setReadOnly(intelTargetRadioNumber.getValue());
            intelPlainText.setReadOnly(intelTargetRadioNumber.getValue());


        }
    }

    private static class TargetRadioClickBaseEventListener implements Listener<BaseEvent> {
        private final Radio intelTargetRadioNumber;
        private final TextField<String> intelNodeId;
        private final CheckBox intelPlainText;

        TargetRadioClickBaseEventListener(Radio intelTargetRadioNumber, TextField<String> intelNodeId, CheckBox intelPlainText) {
            this.intelTargetRadioNumber = intelTargetRadioNumber;
            this.intelNodeId = intelNodeId;
            this.intelPlainText = intelPlainText;
        }

        @Override
        public void handleEvent(BaseEvent be) {
            if (intelTargetRadioNumber.getValue()) {
                intelNodeId.setValue(Parameters.result.getText());
                intelPlainText.setValue(intelTargetRadioNumber.getValue());
            }
            intelNodeId.setReadOnly(intelTargetRadioNumber.getValue());
            intelPlainText.setReadOnly(intelTargetRadioNumber.getValue());
        }
    }

    private static class TestInputValueAsyncCallback implements AsyncCallback<Value> {
        TestInputValueAsyncCallback() {
        }

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
    }

    private class UpdateEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        UpdateEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(Entity result) {
            box.close();

            try {
                notifyEntityAddedListener(result);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class CancelButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CancelButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final TextField<String> nameField;

        private final EntityCombo intelTargetPoint;
        private final CheckBox intelEnabled;
        private final TextArea intelFormula;
        private final TextField<String> intelNodeId;
        private final CheckBox intelPlainText;

        SubmitButtonEventSelectionListener(TextField<String> nameField, EntityCombo intelTargetPoint, CheckBox intelEnabled, TextArea intelFormula, TextField<String> intelNodeId, CheckBox intelPlainText) {
            this.nameField = nameField;

            this.intelTargetPoint = intelTargetPoint;
            this.intelEnabled = intelEnabled;
            this.intelFormula = intelFormula;
            this.intelNodeId = intelNodeId;
            this.intelPlainText = intelPlainText;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            EntityServiceAsync service = GWT.create(EntityService.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Calculation", "please wait...");
            box.show();

            try {
                Intelligence update = createUpdate();

            service.addUpdateEntity(update, new UpdateEntityAsyncCallback(box));

            } catch (NimbitsException e) {
                box.close();
              FeedbackHelper.showError(e);
            }
        }

        private Intelligence createUpdate() throws NimbitsException {
            Intelligence update;

            EntityName name = CommonFactoryLocator.getInstance().createName(nameField.getValue(), EntityType.calculation);

       if (entity.getEntityType().equals(EntityType.intelligence)) {
            update = (Intelligence)entity;
            update.setTarget( intelTargetPoint.getValue().getBaseEntity().getKey());
            update.setEnabled(intelEnabled.getValue());
            update.setInput(intelFormula.getValue());
            update.setNodeId(intelNodeId.getValue());
        }
        else {
            Entity e = EntityModelFactory.createEntity(name,"", EntityType.intelligence, ProtectionLevel.onlyMe,
                    entity.getKey(), entity.getOwner());
            update = IntelligenceModelFactory.createIntelligenceModel(
                    e,
                    intelEnabled.getValue(),
                    intelTargetPoint.getValue().getId(),
                    intelFormula.getValue(),
                    intelNodeId.getValue(),
                    intelPlainText.getValue(),
                    entity.getKey());
        }
            return update;
        }
    }
}

