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

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class SubscriptionPanel extends NavigationEventProvider {

    private static final int REPEAT_DEFAULT = 30;
    private static final double MIN_VALUE = 5d;
    private static final int WIDTH = 350;
    private static final double MAX_VALUE = 1000d;
    private FormData formdata;
    private VerticalPanel vp;
    private final Entity entity;
    private final Map<SettingType, String> settings;

    private User user;
    public SubscriptionPanel(User user, Entity entity, Map<SettingType, String> settings) {
        this.entity = entity;
        this.settings = settings;
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
        } catch (NimbitsException e) {
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
        ComboBox<DeliveryMethodOption> combo = new ComboBox<DeliveryMethodOption>();

        List<DeliveryMethodOption> ops = new ArrayList<DeliveryMethodOption>(SubscriptionNotifyMethod.values().length);

        DeliveryMethodOption none = new DeliveryMethodOption(SubscriptionNotifyMethod.none);
        ops.add(none);
        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.email));
        ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.feed));

        if (settings.containsKey(SettingType.twitterClientId) && !Utils.isEmptyString(settings.get(SettingType.twitterClientId))) {
            ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.twitter));
        }
        if (settings.containsKey(SettingType.facebookAPIKey) && !Utils.isEmptyString(settings.get(SettingType.facebookAPIKey))) {
            ops.add(new DeliveryMethodOption(SubscriptionNotifyMethod.facebook));
        }

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

    private void createForm() throws NimbitsException {

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

        final CheckBox enabled = new CheckBox();

        final SpinnerField spinnerField = new SpinnerField();
        spinnerField.setIncrement(MIN_VALUE);
        spinnerField.getPropertyEditor().setType(Double.class);
        spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("00"));
        spinnerField.setFieldLabel("Repeat limit (Minutes)");
        spinnerField.setMinValue(MIN_VALUE);
        spinnerField.setMaxValue(MAX_VALUE);
        // int alertSelected = (subscription == null) ? SubscriptionNotifyMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();
        SubscriptionType type;
        SubscriptionNotifyMethod method;
        if (entity.getEntityType().equals(EntityType.subscription)) {
            Subscription subscription = (Subscription) entity;
            type= subscription.getSubscriptionType();
            method =subscription.getNotifyMethod();
            subscriptionName.setValue(entity.getName().getValue());
            spinnerField.setValue(subscription.getMaxRepeat());

            machine.setValue(subscription.getNotifyFormatJson());
            machine.setLabelSeparator("");
            machine.setEnabled(subscription.getNotifyMethod().isJsonCompatible());
            enabled.setValue(subscription.getEnabled());


        }
        else {
            type = SubscriptionType.none;
            method = SubscriptionNotifyMethod.none;
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
                "<br><p><b>Be Careful!</b> Subscribing to any new value on a data point that is updated at a " +
                "high frequency can result in hundreds of emails or posts to your social network! Select a high " +
                "update frequency to limit the number of alerts you receive</p>" +
                "<br><p> In order to receive facebook, twitter and IM message, " +
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
                    SubscriptionType subscriptionType =  typeCombo.getValue().getMethod();
                    try {
                        EntityName name = CommonFactoryLocator.getInstance().createName(subscriptionName.getValue(), EntityType.subscription);


                        final Subscription update;

                        if (entity.getEntityType().equals(EntityType.subscription)) {

                            update = (Subscription) entity;
                            update.setName(name);
                            update.setEnabled(enabled.getValue());
                            update.setSubscriptionType(subscriptionType);
                            update.setNotifyMethod(subscriptionNotifyMethod);
                            update.setNotifyFormatJson(machine.getValue());
                            update.setMaxRepeat(spinnerField.getValue().doubleValue());
                            update.setLastSent(new Date());


                        }
                        else {


                            String parent = user.getKey().equals(entity.getOwner()) ? entity.getKey() :  "";

                            Entity newEntity = EntityModelFactory.createEntity(name, "", EntityType.subscription
                                    , ProtectionLevel.onlyMe, parent, "");
                            update = SubscriptionFactory.createSubscription(
                                    newEntity,
                                    entity.getKey(),
                                    subscriptionType,
                                    subscriptionNotifyMethod,
                                    spinnerField.getValue().doubleValue(),
                                    new Date(0),
                                    machine.getValue(),
                                    enabled.getValue());
                        }

                        EntityServiceAsync service = GWT.create(EntityService.class);
                        service.addUpdateEntity(update, new UpdateEntityAsyncCallback(box));



                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }
                }


    }
}
