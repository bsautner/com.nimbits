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
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.connection.ConnectionModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;


public class ConnectionPanel extends BasePanel {
    private final Entity entity;


    public ConnectionPanel(PanelEvent listener, final Entity entity) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_connections.jsp\">Learn More: Connections Help</a>");
        this.entity = entity;
        createForm();
    }


    protected void createForm() {

        final TextField<String> email = new TextField<String>();


        email.setFieldLabel("Email");


        if (entity.getEntityType().equals(EntityType.connection)) {
            Connection e = (ConnectionModel) entity;
            email.setValue(e.getTargetEmail());
            email.setReadOnly(true);


        } else {
            email.setValue("");


        }

        submit.addSelectionListener(new SubmitEventSelectionListener(email));


        Html h = new Html("<p>This will send an email requesting another user to allow your accounts to be connected. You will " +
                "be able to view their entity tree and they will be able you view all of yours including all of the date being logged.</p>" +
                "<br/>" +
                "<p>When the recipient approves the request, you will be able to expand the connection and see their objects in your tree. If this is your own server be " +
                "sure to set the admin email account.  If your not using google app engine, be sure to set the outgoing smpt server settings.");

        vp.add(h);


        simple.setHeaderVisible(false);
        simple.add(email, formdata);


        super.completeForm();


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
            FeedbackHelper.showInfo("An email has been sent to your connection.  Once they approve it, you will see their objects in your tree under the " +
                    "connection object we just added. They will see a similar item containing your points, calculations, etc.");
            try {
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitEventSelectionListener extends SelectionListener<ButtonEvent> {

        final TextField<String> email;


        SubmitEventSelectionListener(TextField<String> email) {
            this.email = email;


        }

        public void componentSelected(ButtonEvent buttonEvent) {

            EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);

            String e = email.getValue();


            final MessageBox box = MessageBox.wait("Progress",
                    "Sending request to " + e, "please wait...");
            box.show();
            try {

                EntityName name = CommonFactory.createName(e, EntityType.connection);

                Connection update;

                if (entity.getEntityType().equals(EntityType.connection)) {


                    FeedbackHelper.showError(new Exception("Connection Requests can't be modified once created, you can delete it if you like"));


                } else {



                    update = new ConnectionModel.Builder()
                            .name(name)
                            .id(entity.getId())
                            .owner(entity.getOwner())
                            .targetEmail(e)
                            .create();


                    if (update != null) {
                        update.setName(name);
                        service.addUpdateEntityRpc(update, new UpdateEntityAsyncCallback(box));
                    }


                }
            } catch (Exception ex) {
                FeedbackHelper.showError(ex);
            }


        }
    }


}


