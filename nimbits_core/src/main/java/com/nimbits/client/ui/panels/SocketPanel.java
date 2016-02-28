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
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.socket.SocketModel;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;


public class SocketPanel extends BasePanel {
    private final Entity entity;


    public SocketPanel(PanelEvent listener, final Entity entity) {
        super(listener, "<a href=\"http://www.nimbits.com/howto_outbound_sockets.jsp\">Learn More</a>");
        this.entity = entity;
        createForm();
    }


    protected void createForm() {

        final TextField<String> targetApiKey = new TextField<String>();
        final TextField<String> targetUrl = new TextField<String>();
        final TextField<String> targetPath = new TextField<String>();
        final TextField<String> extraParams = new TextField<String>();

        targetApiKey.setFieldLabel("Target API Key");
        targetUrl.setFieldLabel("Target URL");
        targetPath.setFieldLabel("Target Path");
        extraParams.setFieldLabel("Extra Parameters");


        if (entity.getEntityType().equals(EntityType.socket)) {
            Socket e = (SocketModel) entity;
            targetApiKey.setValue(e.getTargetApiKey());
            targetUrl.setValue(e.getTargetUrl());
            targetPath.setValue(e.getTargetPath());
            extraParams.setValue(e.getExtraParams());

        } else {
            targetApiKey.setValue("");
            targetUrl.setValue("");
            targetPath.setValue("");
            extraParams.setValue("");

        }

        submit.addSelectionListener(new SubmitEventSelectionListener(targetApiKey, targetUrl, targetPath, extraParams));


        Html h = new Html("<p>This server can open an outbound web socket to another nimbits instance. After creating the socket, " +
                "use the subscription option to configure a data point to send new values out to that server.</p>");


        vp.add(h);


        simple.setHeaderVisible(false);
        simple.add(targetUrl, formdata);
        simple.add(targetPath, formdata);
        simple.add(extraParams, formdata);
        simple.add(targetApiKey, formdata);


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
            try {
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class SubmitEventSelectionListener extends SelectionListener<ButtonEvent> {

        final TextField<String> targetApiKey;
        final TextField<String> targetUrl;
        final TextField<String> targetPath;
        final TextField<String> extraParams;

        SubmitEventSelectionListener(TextField<String> targetApiKey, TextField<String> targetUrl, TextField<String> targetPath, TextField<String> extraParams) {
            this.targetApiKey = targetApiKey;
            this.targetUrl = targetUrl;
            this.targetPath = targetPath;
            this.extraParams = extraParams;

        }

        public void componentSelected(ButtonEvent buttonEvent) {

            EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);

            String api = targetApiKey.getValue();
            String url = targetUrl.getValue();
            String path = targetPath.getValue();
            String params = extraParams.getValue();

            if (api.contains("appspot.com") || api.contains("cloud.nimbits.com")) {
                FeedbackHelper.showError(new Exception("App Engine instances can not accept inbound socket connections. " +
                        "To sync with app engine, use the synchronize option."));
            }

            url = url.replace("http://", "").replace("https://", "").replace("ws://", "");
            final MessageBox box = MessageBox.wait("Progress",
                    "Creating Socket to " + url, "please wait...");
            box.show();
            try {

                EntityName name = CommonFactory.createName(url + " socket", EntityType.socket);

                Socket update;
                SocketModel.Builder builder = new SocketModel.Builder();

                if (entity.getEntityType().equals(EntityType.socket)) {


                    Socket socket = (Socket) entity;
                    builder.init(socket);



                } else {

                    builder.parent(entity.getKey()).owner(entity.getOwner());







                }

                update = builder
                        .targetApiKey(api)
                        .targetUrl(url)
                        .targetPath(path)
                        .extraParams(params)
                        .name(name)
                        .create();


                service.addUpdateEntityRpc(update, new UpdateEntityAsyncCallback(box));

            } catch (Exception ex) {
                FeedbackHelper.showError(ex);
            }


        }
    }


}


