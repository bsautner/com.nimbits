/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.ui.panels;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.cloudplatform.client.enums.subscription.SubscriptionType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModelFactory;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.subscription.Subscription;
import com.nimbits.cloudplatform.client.model.subscription.SubscriptionFactory;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.service.entity.EntityService;
import com.nimbits.cloudplatform.client.service.entity.EntityServiceAsync;
import com.nimbits.cloudplatform.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class SubscriptionPanel extends NavigationEventProvider {

    private static final int REPEAT_DEFAULT = 30;
    private static final double INCREMENT = 1;
    private static final int WIDTH = 350;
    // private static final double MAX_VALUE = 1000d;
    private FormData formdata;
    private VerticalPanel vp;
    private final Entity entity;


    private User user;

    public SubscriptionPanel(final User user, final Entity entity) {
        this.entity = entity;
        this.user = user;


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
        } catch (Exception e) {
            FeedbackHelper.showError(e);
        }


    }


    private static ComboBox<SubscriptionTypeOption> subscriptionTypeOptionComboBox(final String title, final SubscriptionType selectedValue) {
        ComboBox<SubscriptionTypeOption> combo = new ComboBox<SubscriptionTypeOption>();

        List<SubscriptionTypeOption> ops = new ArrayList<SubscriptionTypeOption>(SubscriptionType.values().length);

        for (SubscriptionType type : SubscriptionType.values()) {
            ops.add(new SubscriptionTypeOption(type));
        }

        ListStore<SubscriptionTypeOption> store = new ListStore<SubscriptionTypeOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        SubscriptionTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private ComboBox<DeliveryMethodOption> deliveryMethodComboBox(final String title, final SubscriptionNotifyMethod selectedValue) {
        final ComboBox<DeliveryMethodOption> combo = new ComboBox<DeliveryMethodOption>();

        final List<DeliveryMethodOption> ops = new ArrayList<DeliveryMethodOption>(SubscriptionNotifyMethod.values().length);

        final DeliveryMethodOption none = new DeliveryMethodOption(SubscriptionNotifyMethod.none);
        ops.add(none);
        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.email));
        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.cloud));


        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.instantMessage));

        ListStore<DeliveryMethodOption> store = new ListStore<DeliveryMethodOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        DeliveryMethodOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void createForm() {

        FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);
        final TextField<String> subscriptionName = new TextField<String>();
        subscriptionName.setFieldLabel("Subscription Name");

        final CheckBox machine = new CheckBox();
        machine.setBoxLabel("Send message in JSON format");
        machine.setFieldLabel("");
        final CheckBox enabled = new CheckBox();

        final SpinnerField spinnerField = new SpinnerField();
        spinnerField.setIncrement(INCREMENT);
        spinnerField.getPropertyEditor().setType(Double.class);
        spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("00"));
        spinnerField.setFieldLabel("Repeat limit (Seconds)");
        //spinnerField.setMinValue(MIN_VALUE);
        //spinnerField.setMaxValue(MAX_VALUE);
        // int alertSelected = (subscription == null) ? SubscriptionNotifyMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();
        SubscriptionType type;
        SubscriptionNotifyMethod method;
        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            type = subscription.getSubscriptionType();
            method = subscription.getNotifyMethod();
            subscriptionName.setValue(entity.getName().getValue());
            spinnerField.setValue(subscription.getMaxRepeat());

            machine.setValue(subscription.getNotifyFormatJson());
            machine.setLabelSeparator("");
            machine.setEnabled(subscription.getNotifyMethod().isJsonCompatible());
            enabled.setValue(subscription.getEnabled());


        } else {
            type = SubscriptionType.none;
            method = SubscriptionNotifyMethod.none;
            machine.setLabelSeparator("");
            subscriptionName.setValue(entity.getName().getValue() + " Subscription");
            spinnerField.setValue(REPEAT_DEFAULT);
        }

        final ComboBox<SubscriptionTypeOption> typeCombo = subscriptionTypeOptionComboBox("When this happens", type);
        final ComboBox<DeliveryMethodOption> methodCombo = deliveryMethodComboBox("Relay Data To", method);


        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        cancel.addSelectionListener(new CancelButtonEventSelectionListener());

        submit.addSelectionListener(new SubmitButtonEventSelectionListener(methodCombo, typeCombo, subscriptionName, enabled, machine, spinnerField));


        Html h = new Html("<p>You can subscribe to this data point to receive alerts when it " +
                "receives new data or goes into an alert state (high, low or idle). Select how " +
                "you would like to be notified when this data point receives new data.</p> " +
                "<br><p> In order to receive instant messages, " +
                "you must enable them on the main menu.");


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");


        methodCombo.addSelectionChangedListener(new DeliveryMethodOptionSelectionChangedListener(machine));


        vp.add(h);
        vp.add(pn);
        simple.add(subscriptionName, formdata);
        simple.add(typeCombo, formdata);
        simple.add(methodCombo, formdata);
        simple.add(spinnerField, formdata);
        simple.add(enabled, formdata);
        simple.add(machine, formdata);

        LayoutContainer c = new LayoutContainer();
        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(5));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        c.setLayout(layout);
        cancel.setWidth(100);
        submit.setWidth(100);
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        c.add(cancel, layoutData);
        c.add(submit, layoutData);


        vp.add(simple);
        vp.add(c);
    }


    private static class DeliveryMethodOption extends BaseModelData {
        SubscriptionNotifyMethod method;

        DeliveryMethodOption() {

        }

        DeliveryMethodOption(SubscriptionNotifyMethod value) {
            this.method = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public SubscriptionNotifyMethod getMethod() {
            return method;
        }
    }

    private static class DeliveryMethodOptionSelectionChangedListener extends SelectionChangedListener<DeliveryMethodOption> {
        private final CheckBox machine;

        DeliveryMethodOptionSelectionChangedListener(CheckBox machine) {
            this.machine = machine;
        }

        @Override
        public void selectionChanged(SelectionChangedEvent<DeliveryMethodOption> deliveryMethodOptionSelectionChangedEvent) {
            setMachineEnabled(deliveryMethodOptionSelectionChangedEvent.getSelectedItem().getMethod());
        }

        private void setMachineEnabled(SubscriptionNotifyMethod method) {
            machine.setEnabled(method.isJsonCompatible());
            if (!method.isJsonCompatible()) {
                machine.setValue(false);
            }
        }
    }

    private static class SubscriptionTypeOption extends BaseModelData {
        SubscriptionType type;

        SubscriptionTypeOption() {

        }

        SubscriptionTypeOption(SubscriptionType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public SubscriptionType getMethod() {
            return type;
        }
    }

    private class UpdateEntityAsyncCallback implements AsyncCallback<List<Entity>> {

        final MessageBox box;

        UpdateEntityAsyncCallback(final MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(Throwable caught) {
            box.close();
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(List<Entity> result) {
            try {
                box.close();
                notifyEntityAddedListener(result);
            } catch (Exception e) {
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
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final ComboBox<DeliveryMethodOption> methodCombo;
        private final ComboBox<SubscriptionTypeOption> typeCombo;
        private final TextField<String> subscriptionName;
        private final CheckBox enabled;
        private final CheckBox machine;
        private final SpinnerField spinnerField;

        SubmitButtonEventSelectionListener(ComboBox<DeliveryMethodOption> methodCombo, ComboBox<SubscriptionTypeOption> typeCombo, TextField<String> subscriptionName, CheckBox enabled, CheckBox machine, SpinnerField spinnerField) {
            this.methodCombo = methodCombo;
            this.typeCombo = typeCombo;
            this.subscriptionName = subscriptionName;
            this.enabled = enabled;
            this.machine = machine;
            this.spinnerField = spinnerField;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {

            final MessageBox box = MessageBox.wait("Progress",
                    "Subscribing to your point", "loading...");
            box.show();

            SubscriptionNotifyMethod subscriptionNotifyMethod = methodCombo.getValue().getMethod();
            SubscriptionType subscriptionType = typeCombo.getValue().getMethod();
            try {
                EntityName name = CommonFactory.createName(subscriptionName.getValue(), EntityType.subscription);


                final Subscription update;

                if (entity.getEntityType().equals(EntityType.subscription)) {

                    update = (Subscription) entity;
                    update.setName(name);
                    update.setEnabled(enabled.getValue());
                    update.setSubscriptionType(subscriptionType);
                    update.setNotifyMethod(subscriptionNotifyMethod);
                    update.setNotifyFormatJson(machine.getValue());
                    update.setMaxRepeat(spinnerField.getValue().intValue());


                } else {


                    String parent = user.getKey().equals(entity.getOwner()) ? entity.getKey() : "";

                    Entity newEntity = EntityModelFactory.createEntity(name, "", EntityType.subscription
                            , ProtectionLevel.onlyMe, parent, "");
                    update = SubscriptionFactory.createSubscription(
                            newEntity,
                            entity.getKey(),
                            subscriptionType,
                            subscriptionNotifyMethod,
                            spinnerField.getValue().intValue(),
                            machine.getValue(),
                            enabled.getValue());
                }

                EntityServiceAsync service = GWT.create(EntityService.class);
                service.addUpdateEntityRpc(Arrays.<Entity>asList(update), new UpdateEntityAsyncCallback(box));


            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }


    }
}
