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
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

public class AlertPanel extends BasePanel {


    private final CheckBox he = new CheckBox();
    private final CheckBox idleOn = new CheckBox();
    private final CheckBox deltaOn = new CheckBox();


    private final CheckBox le = new CheckBox();
    private final NumberField high = new NumberField();
    private final NumberField idleSeconds = new NumberField();

    private final NumberField deltaSeconds = new NumberField();
    private final NumberField deltaAlert = new NumberField();

    private final NumberField low = new NumberField();

    private final Entity entity;




    public AlertPanel(final User user, BasePanel.PanelEvent listener, final Entity entity) {
        super(user, listener, "<a href=\"http://www.nimbits.com/howto_alerts.jsp\">Learn More: Data Points</a>");
        this.entity = entity;

        createForm();
    }


    private void createForm() {
        final Point point = (Point) entity;

        high.setFieldLabel("High Value");
        high.setValue(point.getHighAlarm());
        high.setAllowBlank(false);
        simple.add(high, formdata);


        he.setBoxLabel("High alert enabled");
        he.setLabelSeparator("");
        he.setValue(point.isHighAlarmOn());

        simple.add(he, formdata);


        low.setFieldLabel("Low Value");
        low.setAllowBlank(false);
        low.setValue(point.getLowAlarm());

        simple.add(low, formdata);


        le.setBoxLabel("Low alert enabled");
        le.setLabelSeparator("");
        le.setValue(point.isLowAlarmOn());

        simple.add(le, formdata);

        idleOn.setBoxLabel("Idle alert enabled");
        idleOn.setLabelSeparator("");

        idleOn.setValue(point.isIdleAlarmOn());

        idleSeconds.setFieldLabel("Idle Seconds");
        idleSeconds.setValue(point.getIdleSeconds());


        simple.add(idleSeconds, formdata);
        simple.add(idleOn, formdata);


        deltaOn.setBoxLabel("Delta alert enabled");
        deltaOn.setLabelSeparator("");

        deltaOn.setValue(point.isDeltaAlarmOn());

        deltaSeconds.setFieldLabel("Delta Seconds");
        deltaSeconds.setValue(point.getDeltaSeconds());


        deltaAlert.setFieldLabel("Delta Alert Value");
        deltaAlert.setValue(point.getDeltaAlarm());


        simple.add(deltaAlert, formdata);
        simple.add(deltaSeconds, formdata);
        simple.add(deltaOn, formdata);


        if (point.isIdleAlarmOn()) {
            final Html h2 = new Html();

            String s = "<P>Based on the current settings, this point is currently ";
            if (point.idleAlarmSent()) {
                s += "idle.";
            } else {
                s += "not idle.";
            }
            //  s += " Last recorded timestamp was: " + point.getLastRecordedTimestamp() + "</p>";
            h2.setHtml(s);

            simple.add(h2, formdata);
        }


        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {

                try {
                    savePoint();
                } catch (Exception e) {
                    FeedbackHelper.showError(e);
                }

            }
        });

        completeForm();


    }


    private void savePoint() {
        final MessageBox box = MessageBox.wait("Progress",
                "Saving your data, please wait...", "Saving...");
        box.show();

        {
            //General
            final Point point = (Point) entity;


            point.setHighAlarm(high.getValue().doubleValue());
            point.setLowAlarm(low.getValue().doubleValue());
            point.setHighAlarmOn(he.getValue());
            point.setLowAlarmOn(le.getValue());

            //idlealarm
            point.setIdleAlarmOn(idleOn.getValue());
            point.setIdleSeconds(idleSeconds.getValue().intValue());
            point.setIdleAlarmSent(false);
//
            point.setDeltaAlarm(deltaAlert.getValue().doubleValue());
            point.setDeltaAlarmOn(deltaOn.getValue());
            point.setDeltaSeconds(deltaSeconds.getValue().intValue());
            // point.setSendAlertsAsJson(sendAlertAsJson.getValue());


            final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
            // PointServiceAsync service = GWT.create(PointService.class);
            service.addUpdateEntityRpc(user, point, new AsyncCallback<Entity>() {
                @Override
                public void onFailure(final Throwable caught) {
                    box.close();

                    FeedbackHelper.showError(caught);


                }

                @Override
                public void onSuccess(final Entity result) {

                    try {

                        MessageBox.alert("Success", "Point Updated", null);
                    } catch (Exception e) {
                        FeedbackHelper.showError(e);
                    }
                    box.close();
                }
            });
        }
    }


//        h.setHtml("<P>Enter values that will trigger an high or low alert if the value of this point goes above or below a value and/or" +
//                " the number of minutes this point can go without receiving a new value before it goes into an idle alert state. </p>" +
//                "<BR><P>Right click on this point and select \"subscribe\" to configure how you'd like to be alerted to changes in this point's alert state. " +
//                "Other users who subscribe to this point will also receive alerts based on their settings.</P><BR><BR>");
//
//
//        simple.add(h);


}
