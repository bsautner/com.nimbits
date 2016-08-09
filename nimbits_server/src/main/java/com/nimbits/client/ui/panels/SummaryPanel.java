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
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.summary.SummaryModel;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.List;

public class SummaryPanel extends BasePanel {


    private static final double MAX_VALUE = 31556926d;
    private static final int SECONDS_IN_HOUR = 3600;

    private final Entity entity;


    public SummaryPanel(User user, PanelEvent listener, Entity entity) {
        super(user, listener, "<a href=\"http://www.nimbits.com/howto_summary.jsp\">Learn More: Summary and Statistics</a>");
        this.entity = entity;

        createForm();


    }


    private static ComboBox<SummaryTypeOption> summaryTypeOptionComboBox(final String title, final SummaryType selectedValue) {
        ComboBox<SummaryTypeOption> combo = new ComboBox<SummaryTypeOption>();

        List<SummaryTypeOption> ops = new ArrayList<SummaryTypeOption>(SummaryType.values().length);

        for (SummaryType type : SummaryType.values()) {
            ops.add(new SummaryTypeOption(type));
        }

        ListStore<SummaryTypeOption> store = new ListStore<SummaryTypeOption>();

        store.add(ops);

        combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        SummaryTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void createForm() {


        CheckBox enabled = new CheckBox();
        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        final TextField<String> summaryName = new TextField<String>();
        summaryName.setFieldLabel("Summary Name");
        final SpinnerField spinnerField = new SpinnerField();
        spinnerField.setIncrement(1d);
        spinnerField.getPropertyEditor().setType(Double.class);
        spinnerField.getPropertyEditor().setFormat(NumberFormat.getFormat("00"));
        spinnerField.setFieldLabel("Timespan (Seconds)");
        spinnerField.setMinValue(1d);
        spinnerField.setMaxValue(MAX_VALUE);


        SummaryType type;
        String target = null;

        if (entity.getEntityType().equals(EntityType.summary)) {
            Summary summary = (Summary) entity;
            summaryName.setValue(entity.getName().getValue());
            enabled.setValue(((Summary) entity).isEnabled());
            type = summary.getSummaryType();
            spinnerField.setValue(summary.getSummaryIntervalSeconds());
            target = summary.getTarget();
        } else {
            summaryName.setValue(entity.getName().getValue() + " Summary");
            type = SummaryType.average;
            spinnerField.setValue(SECONDS_IN_HOUR);
            enabled.setValue(true);

        }

        final EntityName name = CommonFactory.createName(summaryName.getValue(), EntityType.summary);


        // int alertSelected = (subscription == null) ? SubscriptionNotifyMethod.none.getCode() : subscription.getAlertNotifyMethod().getCode();


        final ComboBox<SummaryTypeOption> typeCombo = summaryTypeOptionComboBox("Summary Type", type);


        final EntityCombo targetCombo = new EntityCombo(user, EntityType.point, target, MESSAGE_SELECT_POINT);
        targetCombo.setFieldLabel("Target");

        submit.addSelectionListener(new SubmitEventSelectionListener(typeCombo, spinnerField, targetCombo, name, enabled));


        Html pn = new Html("<p><b>Name: </b>" + entity.getName().getValue() + "</p>");

        vp.add(pn);
        simple.add(summaryName, formdata);
        simple.add(typeCombo, formdata);

        simple.add(spinnerField, formdata);
        simple.add(targetCombo, formdata);
        simple.add(enabled, formdata);

        completeForm();

    }


    private static class SummaryTypeOption extends BaseModelData {
        SummaryType type;

        @SuppressWarnings("unused")
        SummaryTypeOption() {
        }

        SummaryTypeOption(SummaryType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public SummaryType getMethod() {
            return type;
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
            box.close();
            try {
                notifyEntityAddedListener(null);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }

        @Override
        public void onSuccess(Entity result) {
            box.close();
            try {
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final ComboBox<SummaryTypeOption> typeCombo;
        private final SpinnerField spinnerField;
        private final EntityCombo targetCombo;
        private final EntityName name;
        private final CheckBox enabled;

        SubmitEventSelectionListener(ComboBox<SummaryTypeOption> typeCombo, SpinnerField spinnerField, EntityCombo targetCombo, EntityName name, CheckBox enabled) {
            this.typeCombo = typeCombo;
            this.spinnerField = spinnerField;
            this.targetCombo = targetCombo;
            this.name = name;
            this.enabled = enabled;
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Create Summary", "please wit...");
            box.show();

            SummaryType summaryType = typeCombo.getValue().getMethod();

            Summary update;

            if (entity.getEntityType().equals(EntityType.summary)) {

                try {
                    Trigger summary = (Trigger) entity;
                    entity.setName(name);

                    update = new SummaryModel.Builder()
                            .init((Summary) entity)
                            .target(summary.getTarget())
                            .enabled(enabled.getValue())
                            .summaryType(summaryType)
                            .summaryIntervalMs((long) (spinnerField.getValue().intValue() * 1000))

                            .create();

//                    update = SummaryModelFactory.createSummary(entity,
//                            EntityModelFactory.createTrigger(summary.getTrigger()), EntityModelFactory.createTarget(summary.getTarget()), enabled.getValue(), summaryType,
//                            spinnerField.getValue().intValue() * 1000, new Date());


                    service.addUpdateEntityRpc(user, update, new UpdateEntityAsyncCallback(box));
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

            } else {
                try {

                    update = new SummaryModel.Builder().name(name).parent(entity.getId()).owner(entity.getOwner())
                            .target(targetCombo.getValue().getId())
                            .trigger(entity.getId())
                            .enabled(enabled.getValue())
                            .summaryType(summaryType)
                            .summaryIntervalMs((long) (spinnerField.getValue().intValue() * 1000))


                            .create();


                    if (update != null) {
                        update.setName(name);
                        service.addUpdateEntityRpc(user, update, new UpdateEntityAsyncCallback(box));
                    }

                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

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
}


