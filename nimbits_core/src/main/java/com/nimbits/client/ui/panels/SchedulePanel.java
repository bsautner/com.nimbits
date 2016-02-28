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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.schedule.ScheduleModel;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SchedulePanel extends BasePanel {
    private final Entity entity;
    Logger logger = Logger.getLogger(SchedulePanel.class.getName());
    DateTimeFormat fmt = DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME);

    public SchedulePanel(PanelEvent listener, final Entity entity) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_schedule.jsp\">Learn More: Schedule Help</a>");
        this.entity = entity;
        createForm();
    }


    private void createForm() {


        final NumberField interval = new NumberField();
        TextField<String> dateSelector = new TextField<String>();

        final CheckBox enabled = new CheckBox();
        final TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel("Schedule Name");

        String source = null;
        String target = null;


        if (entity.getEntityType().equals(EntityType.schedule)) {
            nameField.setValue(entity.getName().getValue());
            source = ((Schedule) entity).getSource();
            target = ((Schedule) entity).getTarget();

            enabled.setValue(((Schedule) entity).isEnabled());
            interval.setValue(((Schedule) entity).getInterval() / (60 * 1000));
            dateSelector.setValue(fmt.format(new Date(((Schedule) entity).getLastProcessed())));


        } else {

            enabled.setValue(true);
            dateSelector.setValue(fmt.format(new Date()));


        }


        final EntityCombo targetCombo = new EntityCombo(EntityType.point, target, "Target");
        targetCombo.setFieldLabel("Target");

        final EntityCombo sourceCombo = new EntityCombo(EntityType.point, source, "Source");
        sourceCombo.setFieldLabel("Source");

        enabled.setBoxLabel("Enabled");
        enabled.setLabelSeparator("");

        interval.setFieldLabel("Interval (Minutes)");

        dateSelector.setFieldLabel("Init Start Time");


        submit.addSelectionListener(new SubmitButtonEventSelectionListener(nameField, sourceCombo, targetCombo, enabled, interval, dateSelector));


        simple.add(nameField, formdata);
        simple.add(sourceCombo, formdata);
        simple.add(targetCombo, formdata);
        simple.add(dateSelector, formdata);

        simple.add(interval, formdata);


        simple.add(enabled, formdata);

        super.completeForm();

    }

    private Schedule createSchedule(final EntityName name,
                                    final EntityCombo sourceCombo,
                                    final EntityCombo targetCombo,
                                    final CheckBox enabledCheckbox,
                                    final NumberField interval,
                                    final TextField<String> dateField
    ) {
        Schedule schedule;


        final String source = sourceCombo.getValue() == null ? null : sourceCombo.getValue().getKey();
        final String target = targetCombo.getValue() == null ? null : targetCombo.getValue().getKey();

        ScheduleModel.Builder builder = new ScheduleModel.Builder();

        if (entity.getEntityType().equals(EntityType.schedule)) {

            schedule = (Schedule) entity;
            builder.init(schedule);

        } else {
            builder.name(name).parent(entity.getKey()).owner(entity.getOwner());
        }

        builder.enabled(enabledCheckbox.getValue())
                .interval(interval.getValue().longValue())
                .source(source)
                .target(target)
                .enabled(enabledCheckbox.getValue())
                .lastProcessed(fmt.parse(dateField.getValue()).getTime());



        return builder.create();
    }


    private class AddScheduleEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        AddScheduleEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(final Throwable e) {
            logger.log(Level.SEVERE, "error in rpc", e);
            box.close();
            FeedbackHelper.showError(e);
            try {
                notifyEntityAddedListener(null);
            } catch (Exception e1) {
                FeedbackHelper.showError(e);
            }
        }

        @Override
        public void onSuccess(final Entity result) {
            logger.info("successful callback AddScheduleEntityAsyncCallback");
            box.close();

            try {
                logger.info("successful callback AddScheduleEntityAsyncCallback 2");
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final TextField<String> nameField;
        private final EntityCombo sourceCombo;
        private final EntityCombo targetCombo;
        private final CheckBox enabledCheckbox;
        private final NumberField interval;
        private final TextField<String> dateSelector;

        SubmitButtonEventSelectionListener(TextField<String> nameField, EntityCombo sourceCombo, EntityCombo targetcombo,
                                           CheckBox enabled, NumberField interval, TextField<String> dateSelector) {
            this.nameField = nameField;
            this.sourceCombo = sourceCombo;
            this.targetCombo = targetcombo;
            this.interval = interval;

            this.enabledCheckbox = enabled;
            this.dateSelector = dateSelector;


        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Schedule", "please wait...");
            //box.show();
            final Schedule update;
            logger.log(Level.INFO, "doing update");
            final EntityName entityName = CommonFactory.createName(nameField.getValue(), EntityType.schedule);

            update = createSchedule(entityName, sourceCombo, targetCombo, enabledCheckbox, interval, dateSelector);
            logger.log(Level.INFO, "created schedule");

            Date date = fmt.parse(dateSelector.getValue());


            logger.log(Level.INFO, "DATE LOGGED: " + date.getTime() + "  " + date);
            // Date combined = new Date(date.getTime() + time.getTime() - 1);
            // update.setLastProcessed(date.getTime());
            logger.log(Level.INFO, "doing rpc");
            service.addUpdateEntityRpc(update, new AddScheduleEntityAsyncCallback(box));

        }
    }
}
