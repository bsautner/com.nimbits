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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.subscription.SubscriptionNotifyMethod;
import com.nimbits.client.enums.subscription.SubscriptionType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.List;


public class SubscriptionPanel extends BasePanel {

    private static final int REPEAT_DEFAULT = 30;
    private static final double INCREMENT = 1;
    private final Entity entity;
    private final User user;


    public SubscriptionPanel(PanelEvent listener, final Entity entity, User user) {

        super(listener, "<a href=\"http://www.nimbits.com/howto_subscribe.jsp\">Learn More: Subscription Help</a>");
        this.user = user;
        this.entity = entity;
        createForm();
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


        for (SubscriptionNotifyMethod method : SubscriptionNotifyMethod.values()) {

            ops.add(new DeliveryMethodOption(method));

        }


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


        final TextField<String> subscriptionName = new TextField<String>();
        subscriptionName.setFieldLabel("Subscription Name");

        final TextField<String> target = new TextField<String>();
        target.setFieldLabel("Send To (optional)");

        final EntityCombo webHookCombo;



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
            target.setValue(subscription.getTarget());
            spinnerField.setValue(subscription.getMaxRepeat());

            machine.setValue(subscription.getNotifyFormatJson());
            machine.setLabelSeparator("");
            machine.setEnabled(subscription.getNotifyMethod().isJsonCompatible());
            enabled.setValue(subscription.getEnabled());



            if (subscription.getNotifyMethod().equals(SubscriptionNotifyMethod.webhook)) {
                webHookCombo = new EntityCombo(EntityType.webhook,subscription.getTarget(), "Web Hook Target");


                webHookCombo.setVisible(true);


            }
            else {
                webHookCombo = new EntityCombo(EntityType.webhook,"", "Web Hook Target");
                webHookCombo.setVisible(false);
            }


        } else {
            type = SubscriptionType.none;
            method = SubscriptionNotifyMethod.none;
            machine.setLabelSeparator("");
            subscriptionName.setValue(entity.getName().getValue() + " Subscription");
            spinnerField.setValue(REPEAT_DEFAULT);
            webHookCombo = new EntityCombo(EntityType.webhook,"", "Web Hook Target");
            webHookCombo.setVisible(false);
        }

        webHookCombo.setFieldLabel("Web Hook Target");
        final ComboBox<SubscriptionTypeOption> typeCombo = subscriptionTypeOptionComboBox("When this happens", type);
        final ComboBox<DeliveryMethodOption> methodCombo = deliveryMethodComboBox("Relay Data To", method);


        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        submit.addSelectionListener(new SubmitButtonEventSelectionListener(webHookCombo, methodCombo, typeCombo, subscriptionName, target, enabled, machine, spinnerField));


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");


        methodCombo.addSelectionChangedListener(new DeliveryMethodOptionSelectionChangedListener(machine));


        // target.setVisible(false);

        methodCombo.setFireChangeEventOnSetValue(true);
        methodCombo.addSelectionChangedListener(new SelectionChangedListener<DeliveryMethodOption>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<DeliveryMethodOption> selectionChangedEvent) {
                target.setVisible(selectionChangedEvent.getSelectedItem().getMethod().equals(SubscriptionNotifyMethod.email));
                webHookCombo.setVisible(selectionChangedEvent.getSelectedItem().getMethod().equals(SubscriptionNotifyMethod.webhook));

            }
        });

        vp.add(pn);
        simple.add(subscriptionName, formdata);


        simple.add(typeCombo, formdata);
        simple.add(methodCombo, formdata);
        simple.add(webHookCombo, formdata);
        simple.add(target, formdata);
        simple.add(spinnerField, formdata);

        simple.add(enabled, formdata);
        simple.add(machine, formdata);

        completeForm();


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

    private class UpdateEntityAsyncCallback implements AsyncCallback<Entity> {

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
        public void onSuccess(Entity result) {
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
        private final EntityCombo webhookCombo;
        private final TextField<String> subscriptionName;
        private final TextField<String> target;
        private final CheckBox enabled;
        private final CheckBox machine;
        private final SpinnerField spinnerField;

        SubmitButtonEventSelectionListener(EntityCombo webhookCombo, ComboBox<DeliveryMethodOption> methodCombo, ComboBox<SubscriptionTypeOption> typeCombo, TextField<String> subscriptionName, TextField<String> target, CheckBox enabled, CheckBox machine, SpinnerField spinnerField) {
            this.methodCombo = methodCombo;
            this.typeCombo = typeCombo;
            this.subscriptionName = subscriptionName;
            this.enabled = enabled;
            this.machine = machine;
            this.spinnerField = spinnerField;
            this.target = target;
            this.webhookCombo = webhookCombo;
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


                    if (subscriptionNotifyMethod.equals(SubscriptionNotifyMethod.webhook)) {
                        update.setTarget(webhookCombo.getValue().getKey());
                    }
                    else {
                        update.setTarget(target.getValue());
                    }


                } else {


                    String parent = user.getKey().equals(entity.getOwner()) ? entity.getKey() : "";



                    String targetValue;
                    if (subscriptionNotifyMethod.equals(SubscriptionNotifyMethod.webhook)) {
                        targetValue = (webhookCombo.getValue().getKey());
                    }
                    else {
                        targetValue = (target.getValue());
                    }

                    update = new SubscriptionModel.Builder()
                            .name(name)
                            .parent(parent)
                            .subscriptionType(subscriptionType)
                            .subscribedEntity(entity.getKey())
                            .notifyMethod(subscriptionNotifyMethod)
                            .maxRepeat(spinnerField.getValue().intValue())
                            .target(targetValue)


                            .create();

//                    update = SubscriptionFactory.createSubscription(
//                            newEntity,
//                            entity.getKey(),
//                            subscriptionType,
//                            subscriptionNotifyMethod,
//                            spinnerField.getValue().intValue(),
//                            machine.getValue(),
//                            enabled.getValue(),
//                            targetValue);
                }

                EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
                service.addUpdateEntityRpc(update, new UpdateEntityAsyncCallback(box));


            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }


    }
}
